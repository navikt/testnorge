package no.nav.dolly.bestilling.sykemelding;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.personservice.PersonServiceConsumer;
import no.nav.dolly.bestilling.sykemelding.domain.DetaljertSykemeldingRequest;
import no.nav.dolly.bestilling.sykemelding.domain.TsmSykemeldingRequest;
import no.nav.dolly.bestilling.sykemelding.dto.NySykemeldingResponse;
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
import no.nav.dolly.service.TransactionHelperService;
import no.nav.dolly.service.TransaksjonMappingService;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
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
    private final TsmSykemeldingConsumer tsmSykemeldingConsumer;
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
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling,
                                                DollyPerson dollyPerson,
                                                BestillingProgress progress,
                                                boolean isOpprettEndre) {

        RsSykemelding sykemelding = bestilling.getSykemelding();

        if (isNull(sykemelding)) {
            return Mono.just(progress);
        }

        if (sykemelding.hasNySykemelding()) {
            var nySykemelding = sykemelding.getNySykemelding();
            return setProgress(progress, getGenereringStartet())
                    .then(postNySykemelding(nySykemelding, dollyPerson.getIdent())
                            .map(this::getStatus)
                            .timeout(Duration.ofSeconds(applicationConfig.getClientTimeout()))
                            .onErrorResume(error ->
                                    Mono.just(encodeStatus(WebClientError.describe(error).getMessage()))))
                    .flatMap(status -> oppdaterStatus(progress, status));
        }

        if (sykemelding.hasDetaljertSykemelding()) {

            return transaksjonMappingService.existAlready(
                            SYKEMELDING, dollyPerson.getIdent(), null, bestilling.getId())
                    .flatMap(exists -> {
                        if (BooleanUtils.isTrue(exists) && !isOpprettEndre) {
                            return setProgress(progress, "OK");
                        }
                        return setProgress(progress, getGenereringStartet())
                                .then(getPerson(dollyPerson.getIdent())
                                        .flatMap(persondata ->
                                                postDetaljertSykemelding(sykemelding, persondata)
                                                        .flatMap(resp -> saveTransaksjonId(resp, bestilling.getId())
                                                                .thenReturn(resp))
                                                        .map(this::getStatus))
                                        .timeout(Duration.ofSeconds(applicationConfig.getClientTimeout()))
                                        .onErrorResume(error ->
                                                Mono.just(encodeStatus(WebClientError.describe(error).getMessage())))
                                        .collect(Collectors.joining()))
                                .flatMap(status -> oppdaterStatus(progress, status));
                    });
        }
        return Mono.just(progress);
    }

    @Override
    public void release(List<String> identer) {
        // TODO: slette fra TSM
    }

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress, String status) {
        return transactionHelperService.persister(
                progress,
                BestillingProgress::getSykemeldingStatus,
                BestillingProgress::setSykemeldingStatus,
                status);
    }

    private String getStatus(SykemeldingResponse status) {
        log.info("Sykemelding response for {} mottatt, status: {}", status.getIdent(), status.getStatus());
        return status.getStatus().is2xxSuccessful()
                ? "DetaljertSykemelding:OK"
                : errorStatusDecoder.getErrorText(status.getStatus(), status.getAvvik());
    }

    private String getStatus(NySykemeldingResponse status) {
        log.info("Ny sykemelding response for {} mottatt, {}", status.ident(), Json.pretty(status));
        return isNull(status.error())
                ? "NySykemelding:OK"
                : status.error();
    }

    private Mono<BestillingProgress> setProgress(BestillingProgress progress, String status) {
        return transactionHelperService.persister(
                progress,
                BestillingProgress::getSykemeldingStatus,
                BestillingProgress::setSykemeldingStatus,
                status);
    }

    private Flux<PdlPersonBolk.Data> getPerson(String ident) {
        return personServiceConsumer.getPdlPersoner(List.of(ident))
                .filter(pdlPersonBolk -> nonNull(pdlPersonBolk.getData()))
                .map(PdlPersonBolk::getData);
    }

    private Mono<Norg2EnhetResponse> getNorgenhet(PdlPersonBolk.Data persondata) {
        var geografiskOmrade = persondata.getHentGeografiskTilknytningBolk().stream()
                .map(PdlPersonBolk.GeografiskTilknytningBolk::getGeografiskTilknytning)
                .map(gt -> nonNull(gt) && isNotBlank(gt.getGtType()) ?
                        switch (gt.getGtType()) {
                            case "KOMMUNE" -> gt.getGtKommune();
                            case "BYDEL" -> gt.getGtBydel();
                            default -> null;
                        } : null)
                .collect(Collectors.joining());
        return isNotBlank(geografiskOmrade) ? norg2Consumer.getNorgEnhet(geografiskOmrade) : Mono.empty();
    }

    private Mono<SykemeldingResponse> postDetaljertSykemelding(RsSykemelding sykemelding,
                                                               PdlPersonBolk.Data persondata) {
        return Mono.just(sykemelding)
                .map(RsSykemelding::getDetaljertSykemelding)
                .flatMap(detaljert ->
                        Mono.zip(kodeverkConsumer.getKodeverkByName("Postnummer"), getNorgenhet(persondata))
                                .flatMap(kodeverk -> {
                                    var req = mapperFacade.map(detaljert, DetaljertSykemeldingRequest.class);
                                    var context = new MappingContext.Factory().getContext();
                                    context.setProperty("postnummer", kodeverk.getT1());
                                    context.setProperty("norg2Enhet", kodeverk.getT2());
                                    req.setPasient(mapperFacade.map(
                                            persondata,
                                            DetaljertSykemeldingRequest.Pasient.class,
                                            context));
                                    return sykemeldingConsumer.postDetaljertSykemelding(req);
                                }));
    }

    private Mono<NySykemeldingResponse> postNySykemelding(RsSykemelding.RsNySykemelding rsNySykemelding,
                                                          String ident) {

        var aktivitet = rsNySykemelding.getAktivitet().stream()
                .map(a -> new TsmSykemeldingRequest.Aktivitet(a.getFom(), a.getTom()))
                .collect(Collectors.toList());

        TsmSykemeldingRequest request = new TsmSykemeldingRequest(ident, aktivitet);

        return tsmSykemeldingConsumer.postTsmSykemelding(request);
    }

    private Mono<TransaksjonMapping> saveTransaksjonId(SykemeldingResponse response, Long bestillingId) {
        if (response.getStatus().is2xxSuccessful()) {
            response.getSykemeldingRequest().setSykemeldingId(response.getMsgId());
            return transaksjonMappingService.save(TransaksjonMapping.builder()
                    .ident(response.getIdent())
                    .bestillingId(bestillingId)
                    .transaksjonId(toJson(response.getSykemeldingRequest()))
                    .datoEndret(LocalDateTime.now())
                    .system(SYKEMELDING.name())
                    .miljoe("q1")
                    .build());
        }
        return Mono.empty();
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Feilet å konvertere transaksjonsId for sykemelding");
            return null;
        }
    }
}
