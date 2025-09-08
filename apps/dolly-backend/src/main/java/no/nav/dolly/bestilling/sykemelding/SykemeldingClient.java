package no.nav.dolly.bestilling.sykemelding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.bestilling.sykemelding.domain.DetaljertSykemeldingRequest;
import no.nav.dolly.bestilling.sykemelding.dto.SykemeldingResponse;
import no.nav.dolly.config.ApplicationConfig;
import no.nav.dolly.consumer.kodeverk.KodeverkConsumer;
import no.nav.dolly.consumer.norg2.Norg2Consumer;
import no.nav.dolly.consumer.norg2.dto.Norg2EnhetResponse;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.sykemelding.RsSykemelding;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.TransaksjonMappingService;
import no.nav.dolly.service.TransactionHelperService;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.SYKEMELDING;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static no.nav.dolly.util.DollyTextUtil.getGenereringStartet;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class SykemeldingClient implements ClientRegister {

    private final SykemeldingConsumer sykemeldingConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final TransaksjonMappingService transaksjonMappingService;
    private final MapperFacade mapperFacade;
    private final ObjectMapper objectMapper;
    private final TransactionHelperService transactionHelperService;
    private final PersonServiceConsumer personServiceConsumer;
    private final KodeverkConsumer kodeverkConsumer;
    private final Norg2Consumer norg2Consumer;
    private final ApplicationConfig applicationConfig;

    @Override
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        return Mono.just(bestilling)
                .filter(bestillling -> nonNull(bestillling.getSykemelding()))
                .map(RsDollyUtvidetBestilling::getSykemelding)
                .flatMap(sykemelding -> transaksjonMappingService.existAlready(SYKEMELDING, dollyPerson.getIdent(), null, bestilling.getId())
                        .flatMap(exists -> {

                            if (BooleanUtils.isTrue(exists) && !isOpprettEndre) {
                                return setProgress(progress, "OK");

                            } else {
                                return setProgress(progress, getGenereringStartet())
                                        .then(getPerson(dollyPerson.getIdent())
                                                .flatMap(persondata ->
                                                        postDetaljertSykemelding(sykemelding, persondata)
                                                                .filter(Objects::nonNull)
                                                                .flatMap(status -> saveTransaksjonId(status, bestilling.getId())
                                                                        .thenReturn(status))
                                                                .map(this::getStatus))
                                                .timeout(Duration.ofSeconds(applicationConfig.getClientTimeout()))
                                                .onErrorResume(error -> Mono.just(encodeStatus(WebClientError.describe(error).getMessage())))
                                                .collect(Collectors.joining())
                                                .flatMap(status -> oppdaterStatus(progress, status)));
                            }
                        }));
    }

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress, String status) {

        return transactionHelperService.persister(progress, BestillingProgress::getSykemeldingStatus,
                BestillingProgress::setSykemeldingStatus, status);
    }

    @Override
    public void release(List<String> identer) {

        // Sletting er ikke støttet
    }

    private String getStatus(SykemeldingResponse status) {

        log.info("Sykemelding response for {} mottatt, status: {}", status.getIdent(), status.getStatus());
        return status.getStatus().is2xxSuccessful() ? "OK" :
                errorStatusDecoder.getErrorText(status.getStatus(), status.getAvvik());
    }

    private Mono<BestillingProgress> setProgress(BestillingProgress progress, String status) {

        return transactionHelperService.persister(progress, BestillingProgress::getSykemeldingStatus,
                BestillingProgress::setSykemeldingStatus, status);
    }

    private Flux<PdlPersonBolk.Data> getPerson(String ident) {

        return personServiceConsumer.getPdlPersoner(List.of(ident))
                .filter(pdlPersonBolk -> nonNull(pdlPersonBolk.getData()))
                .map(PdlPersonBolk::getData);
    }

    private Mono<Norg2EnhetResponse> getNorgenhet(PdlPersonBolk.Data persondata) {

        var geografiskOmrade = persondata.getHentGeografiskTilknytningBolk().stream()
                .map(PdlPersonBolk.GeografiskTilknytningBolk::getGeografiskTilknytning)
                .map(geografiskTilknytning -> nonNull(geografiskTilknytning) &&
                        isNotBlank(geografiskTilknytning.getGtType()) ?
                        switch (geografiskTilknytning.getGtType()) {
                            case "KOMMUNE" -> geografiskTilknytning.getGtKommune();
                            case "BYDEL" -> geografiskTilknytning.getGtBydel();
                            default -> null;
                        } : null)
                .collect(Collectors.joining());

        return isNotBlank(geografiskOmrade) ? norg2Consumer.getNorgEnhet(geografiskOmrade) : Mono.empty();
    }

    private Mono<SykemeldingResponse> postDetaljertSykemelding(RsSykemelding sykemelding,
                                                               PdlPersonBolk.Data persondata) {

        return Mono.just(sykemelding)
                .filter(RsSykemelding::hasDetaljertSykemelding)
                .map(RsSykemelding::getDetaljertSykemelding)
                .flatMap(detaljert ->
                        Mono.zip(kodeverkConsumer.getKodeverkByName("Postnummer"), getNorgenhet(persondata))
                                .flatMap(kodeverk -> {

                                    var detaljertSykemeldingRequest =
                                            mapperFacade.map(detaljert,
                                                    DetaljertSykemeldingRequest.class);

                                    var context = new MappingContext.Factory().getContext();
                                    context.setProperty("postnummer", kodeverk.getT1());
                                    context.setProperty("norg2Enhet", kodeverk.getT2());

                                    detaljertSykemeldingRequest.setPasient(mapperFacade.map(persondata,
                                            DetaljertSykemeldingRequest.Pasient.class, context));

                                    return sykemeldingConsumer.postDetaljertSykemelding(detaljertSykemeldingRequest);
                                }));
    }

    private Mono<TransaksjonMapping> saveTransaksjonId(SykemeldingResponse sykemelding, Long bestillingId) {

        if (sykemelding.getStatus().is2xxSuccessful()) {

            log.info("Lagrer transaksjon for {} i q1 ", sykemelding.getIdent());

            sykemelding.getSykemeldingRequest().setSykemeldingId(sykemelding.getMsgId());
            return transaksjonMappingService.save(TransaksjonMapping.builder()
                    .ident(sykemelding.getIdent())
                    .bestillingId(bestillingId)
                    .transaksjonId(toJson(sykemelding.getSykemeldingRequest()))
                    .datoEndret(LocalDateTime.now())
                    .system(SYKEMELDING.name())
                    .miljoe("q1")
                    .build());
        } else {
            return Mono.empty();
        }
    }

    private String toJson(Object object) {

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Feilet å konvertere transaksjonsId for sykemelding");
        }
        return null;
    }
}
