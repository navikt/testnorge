package no.nav.dolly.bestilling.pensjonforvalter.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.PensjonforvalterConsumer;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonVedtakRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonVedtakResponse;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.SystemTyper;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.TransaksjonMappingService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.basicAlderspensjonRequest;
import static no.nav.dolly.domain.resultset.SystemTyper.PEN_AP;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static org.apache.poi.util.StringUtil.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class PensjonforvalterHelper {

    private final ErrorStatusDecoder errorStatusDecoder;
    private final ObjectMapper objectMapper;
    private final PensjonforvalterConsumer pensjonforvalterConsumer;
    private final TransaksjonMappingService transaksjonMappingService;

    @SuppressWarnings("java:S3740")
    public Mono<TransaksjonMapping> saveAPTransaksjonId(String ident, String miljoe, Long bestillingId,
                                                        SystemTyper type, Object vedtak) {

        log.info("Lagrer transaksjon for {} i {} ", ident, miljoe);

        return transaksjonMappingService.delete(ident, miljoe, type.name())
                .then(saveTransaksjonId(ident, miljoe, bestillingId, type, vedtak));
    }

    public Mono<TransaksjonMapping> saveTransaksjonId(String ident, String miljoe, Long bestillingId,
                                                        SystemTyper type, Object vedtak) {

        return transaksjonMappingService.delete(ident, miljoe, type.name())
                .then(transaksjonMappingService.save(
                        TransaksjonMapping.builder()
                                .ident(ident)
                                .bestillingId(bestillingId)
                                .transaksjonId(toJson(vedtak))
                                .datoEndret(LocalDateTime.now())
                                .miljoe(miljoe)
                                .system(type.name())
                                .build()));
    }

    private String toJson(Object object) {

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("Feilet å konvertere transaksjonsId for pensjonForvalter", e);
        }
        return null;
    }

    public String decodeStatus(PensjonforvalterResponse response, String ident) {

        log.info("Mottatt status på {} fra Pensjon-Testdata-Facade: {}", ident, response);

        return response.getStatus().stream()
                .map(entry -> String.format("%s:%s", entry.getMiljo(),
                        entry.getResponse().isResponse2xx() ? "OK" :
                                getError(entry)))
                .collect(Collectors.joining(","));
    }

    public String getError(PensjonforvalterResponse.ResponseEnvironment entry) {

        var response = entry.getResponse();
        var httpStatus = response.getHttpStatus();

        if (isNotBlank(response.getMessage())) {
            if (response.getMessage().contains("{")) {
                return encodeStatus(
                        "Feil: " + response.getMessage().split("\\{")[1].split("}")[0].replace("message\":", ""));
            } else {
                return encodeStatus("Feil: " + response.getMessage());
            }

        } else {
            return errorStatusDecoder.getErrorText(HttpStatus.valueOf(httpStatus.getStatus()), httpStatus.getReasonPhrase());
        }
    }

    public Flux<AlderspensjonVedtakRequest> hentTransaksjonMappingAP(String ident, String miljoe) {

        return transaksjonMappingService.getTransaksjonMapping(PEN_AP.name(), ident, miljoe)
                .map(mapping -> {
                    try {
                        return objectMapper.readValue(mapping.getTransaksjonId(), AlderspensjonVedtakRequest.class);
                    } catch (JsonProcessingException e) {
                        log.error("Feil ved deserialisering av transaksjonId", e);
                        return basicAlderspensjonRequest(ident, Set.of(miljoe));
                    }
                });
    }

    public Mono<PensjonVedtakResponse> hentSisteVedtakAPHvisOK(String ident, String miljoe) {

        return pensjonforvalterConsumer.hentVedtak(ident, miljoe)
                .filter(PensjonVedtakResponse::isSaktypeAP)
                .sort(Comparator.comparing(PensjonVedtakResponse::getFom).reversed())
                .collectList()
                .flatMap(vedtakResponse -> !vedtakResponse.isEmpty() &&
                        vedtakResponse.getFirst().getSisteOppdatering().contains("opprettet") ?
                        Mono.just(vedtakResponse.getFirst()) : Mono.empty());
    }
}
