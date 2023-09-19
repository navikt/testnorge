package no.nav.dolly.bestilling.sykemelding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientFuture;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.sykemelding.domain.DetaljertSykemeldingRequest;
import no.nav.dolly.bestilling.sykemelding.domain.SyntSykemeldingRequest;
import no.nav.dolly.bestilling.sykemelding.dto.Norg2EnhetResponse;
import no.nav.dolly.bestilling.sykemelding.dto.SykemeldingResponse;
import no.nav.dolly.consumer.kodeverk.KodeverkConsumer;
import no.nav.dolly.consumer.pdlperson.PdlPersonConsumer;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.sykemelding.RsSykemelding;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.TransaksjonMappingService;
import no.nav.dolly.util.TransactionHelperService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.resultset.SystemTyper.SYKEMELDING;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class SykemeldingClient implements ClientRegister {

    private final SykemeldingConsumer sykemeldingConsumer;
    private final SyntSykemeldingConsumer syntSykemeldingConsumer;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final TransaksjonMappingService transaksjonMappingService;
    private final MapperFacade mapperFacade;
    private final ObjectMapper objectMapper;
    private final TransactionHelperService transactionHelperService;
    private final PdlPersonConsumer pdlPersonConsumer;
    private final KodeverkConsumer kodeverkConsumer;
    private final Norg2Consumer norg2Consumer;

    @Override
    public Flux<ClientFuture> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        return Flux.just(bestilling)
                .filter(bestillling -> nonNull(bestillling.getSykemelding()))
                .map(RsDollyUtvidetBestilling::getSykemelding)
                .flatMap(sykemelding -> {

                    if (transaksjonMappingService.existAlready(SYKEMELDING, dollyPerson.getIdent(), null) && !isOpprettEndre) {
                        setProgress(progress, "OK");
                        return Mono.empty();

                    } else {
                        setProgress(progress, "Info: Venter på generering av sykemelding ...");
                        long bestillingId = progress.getBestilling().getId();

                        return getPerson(dollyPerson.getIdent())
                                .flatMap(persondata -> Flux.concat(postSyntSykemelding(sykemelding, persondata),
                                                postDetaljertSykemelding(sykemelding, persondata))
                                        .filter(Objects::nonNull)
                                        .doOnNext(status -> saveTransaksjonId(status, bestillingId))
                                        .map(this::getStatus)
                                        .collect(Collectors.joining()))
                                .collect(Collectors.joining())
                                .map(status -> futurePersist(progress, status));
                    }
                });
    }

    private ClientFuture futurePersist(BestillingProgress progress, String status) {

        return () -> {
            transactionHelperService.persister(progress, BestillingProgress::setSykemeldingStatus, status);
            return progress;
        };
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

    private void setProgress(BestillingProgress progress, String status) {

        transactionHelperService.persister(progress, BestillingProgress::setSykemeldingStatus, status);
    }

    private Flux<PdlPersonBolk.Data> getPerson(String ident) {

        return pdlPersonConsumer.getPdlPersoner(List.of(ident))
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

    private Mono<SykemeldingResponse> postSyntSykemelding(RsSykemelding sykemelding, PdlPersonBolk.Data persondata) {

        return Mono.just(sykemelding)
                .filter(RsSykemelding::hasSyntSykemelding)
                .map(RsSykemelding::getSyntSykemelding)
                .flatMap(syntmelding -> {

                    var context = new MappingContext.Factory().getContext();
                    context.setProperty("persondata", persondata);
                    var syntSykemeldingRequest =
                            mapperFacade.map(syntmelding, SyntSykemeldingRequest.class, context);

                    return syntSykemeldingConsumer.postSyntSykemelding(syntSykemeldingRequest);
                });
    }

    private void saveTransaksjonId(SykemeldingResponse sykemelding, Long bestillingId) {

        if (sykemelding.getStatus().is2xxSuccessful()) {

            log.info("Lagrer transaksjon for {} i q1 ", sykemelding.getIdent());

            transaksjonMappingService.save(TransaksjonMapping.builder()
                    .ident(sykemelding.getIdent())
                    .bestillingId(bestillingId)
                    .transaksjonId(toJson(sykemelding.getSykemeldingRequest()))
                    .datoEndret(LocalDateTime.now())
                    .system(SYKEMELDING.name())
                    .miljoe("q1")
                    .build());
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
