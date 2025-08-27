package no.nav.dolly.bestilling.histark;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MappingContext;
import no.nav.dolly.bestilling.ClientRegister;
import no.nav.dolly.bestilling.histark.domain.HistarkRequest;
import no.nav.dolly.bestilling.histark.domain.HistarkResponse;
import no.nav.dolly.bestilling.histark.domain.HistarkTransaksjon;
import no.nav.dolly.config.ApplicationConfig;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.RsDollyUtvidetBestilling;
import no.nav.dolly.domain.resultset.dolly.DollyPerson;
import no.nav.dolly.domain.resultset.histark.RsHistark;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.DokumentService;
import no.nav.dolly.service.TransaksjonMappingService;
import no.nav.dolly.service.TransactionHelperService;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static no.nav.dolly.domain.resultset.SystemTyper.HISTARK;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.getInfoVenter;
import static org.apache.commons.lang3.BooleanUtils.isFalse;

@Slf4j
@Service
@RequiredArgsConstructor
public class HistarkClient implements ClientRegister {

    private final ApplicationConfig applicationConfig;
    private final HistarkConsumer histarkConsumer;
    private final MapperFacade mapperFacade;
    private final TransaksjonMappingService transaksjonMappingService;
    private final ObjectMapper objectMapper;
    private final TransactionHelperService transactionHelperService;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final DokumentService dokumentService;

    @Override
    public Mono<BestillingProgress> gjenopprett(RsDollyUtvidetBestilling bestilling, DollyPerson dollyPerson, BestillingProgress progress, boolean isOpprettEndre) {

        if (isNull(bestilling.getHistark())) {

            return Mono.empty();
        }

        return oppdaterStatus(progress, getInfoVenter(HISTARK.name()))
                .then(transaksjonMappingService.existAlready(HISTARK,
                        dollyPerson.getIdent(), "NA", bestilling.getId()))
                .flatMap(eksisterer -> {
                    if (isFalse(eksisterer) || isOpprettEndre) {
                        return Flux.fromIterable(bestilling.getHistark().getDokumenter())
                                .flatMap(dokument -> buildRequest(dokument, dollyPerson.getIdent(), progress.getBestillingId()))
                                .flatMap(histarkConsumer::postHistark)
                                .collectList()
                                .mapNotNull(status -> getStatus(dollyPerson.getIdent(), bestilling.getId(), status));
                    } else {
                        return Mono.just("OK");
                    }
                })
                .timeout(Duration.ofSeconds(applicationConfig.getClientTimeout()))
                .onErrorResume(this::getErrors)
                .flatMap(status -> oppdaterStatus(progress, status));
    }

    @Override
    public void release(List<String> identer) {

        // Sletting er ikke støttet
    }

    private Mono<BestillingProgress> oppdaterStatus(BestillingProgress progress, String status) {

        return transactionHelperService.persister(progress, BestillingProgress::setHistarkStatus, status);
    }

    private Mono<String> getErrors(Throwable error) {

        return Mono.just(encodeStatus(WebClientError.describe(error).getMessage()));
    }

    private String getStatus(String ident, Long bestillingId, List<HistarkResponse> response) {

        log.info("Histark response {} mottatt for ident {}", response, ident);

        if (isNull(response)) {
            return null;
        }

        saveTransaksjonId(response, ident, bestillingId);

        return response.stream()
                .map(status ->
                        status.isOk() ? "OK: %s".formatted(status.getDokument()) :
                                "FEIL: %s".formatted(encodeStatus(errorStatusDecoder.getStatusMessage(status.getFeilmelding()))))
                .collect(Collectors.joining(","));
    }

    private Mono<HistarkRequest> buildRequest(RsHistark.RsHistarkDokument dokument, String ident, Long bestillingId) {

        return dokumentService.getDokumenterByBestilling(bestillingId)
                .collectList()
                .map(dokumenter -> {
                    var context = new MappingContext.Factory().getContext();
                    context.setProperty("personIdent", ident);
                    context.setProperty("dokumenter", dokumenter);
                    return context;
                })
                .map(context -> mapperFacade.map(dokument, HistarkRequest.class, context));
    }

    private void saveTransaksjonId(List<HistarkResponse> histarkIds, String ident, Long bestillingId) {

        log.info("Lagrer transaksjon for ident {}", ident);

        if (histarkIds.stream().anyMatch(HistarkResponse::isOk)) {

            transaksjonMappingService.save(
                    TransaksjonMapping.builder()
                            .ident(ident)
                            .bestillingId(bestillingId)
                            .transaksjonId(toJson(histarkIds.stream()
                                    .filter(HistarkResponse::isOk)
                                    .map(HistarkResponse::getId)
                                    .map(histarkId -> HistarkTransaksjon.builder()
                                            .dokumentInfoId(histarkId)
                                            .build())
                                    .toList()))
                            .datoEndret(LocalDateTime.now())
                            .miljoe("NA")
                            .system(HISTARK.name())
                            .build());
        }
    }

    private String toJson(Object object) {

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Feilet å konvertere transaksjonsId for histark", e);
        }
        return null;
    }
}
