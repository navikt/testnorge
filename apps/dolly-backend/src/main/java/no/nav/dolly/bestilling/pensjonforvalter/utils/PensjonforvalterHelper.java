package no.nav.dolly.bestilling.pensjonforvalter.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonVedtakDTO;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonTransaksjonId;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.SystemTyper;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.TransaksjonMappingService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.bestilling.pensjonforvalter.utils.PensjonforvalterUtils.basicAlderspensjonRequestDTO;
import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static org.apache.poi.util.StringUtil.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class PensjonforvalterHelper {

    private final ErrorStatusDecoder errorStatusDecoder;
    private final ObjectMapper objectMapper;
    private final TransaksjonMappingService transaksjonMappingService;

    @SuppressWarnings("java:S3740")
    public Mono<TransaksjonMapping> saveUTTransaksjonId(String ident, String miljoe,
                                                        Long bestillingId,
                                                        PensjonTransaksjonId vedtak) {

        return transaksjonMappingService.delete(ident, miljoe, SystemTyper.PEN_UT.name())
                .then(saveTransaksjonId(ident, miljoe, bestillingId, SystemTyper.PEN_UT, vedtak));
    }

    public Mono<TransaksjonMapping> saveAPTransaksjonId(String ident, String miljoe,
                                                        Long bestillingId,
                                                        PensjonTransaksjonId vedtak) {

        return transaksjonMappingService.delete(ident, miljoe, SystemTyper.PEN_AP.name())
                .then(transaksjonMappingService.delete(ident, miljoe, SystemTyper.PEN_AP_REVURDERING.name()))
                .then(transaksjonMappingService.delete(ident, miljoe, SystemTyper.PEN_AP_NY_UTTAKSGRAD.name()))
                .then(saveTransaksjonId(ident, miljoe, bestillingId, SystemTyper.PEN_AP, vedtak));
    }

    public Mono<TransaksjonMapping> saveTransaksjonId(String ident, String miljoe, Long bestillingId,
                                                      SystemTyper type, PensjonTransaksjonId vedtak) {

        log.info("Lagrer transaksjon for {} i {} ", ident, miljoe);
        return transaksjonMappingService.save(
                TransaksjonMapping.builder()
                        .ident(ident)
                        .bestillingId(bestillingId)
                        .transaksjonId(toJson(vedtak))
                        .datoEndret(LocalDateTime.now())
                        .miljoe(miljoe)
                        .system(type.name())
                        .build());
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

    public Mono<AlderspensjonVedtakDTO> hentForrigeVedtakAP(String ident, String miljoe, LocalDate fomDato) {

        return transaksjonMappingService.getTransaksjonMapping(ident, miljoe)
                .filter(transaksjonMapping -> transaksjonMapping.getSystem().contains("AP") &&
                        !transaksjonMapping.getSystem().contains("REVURDERING"))
                .sort(Comparator.comparing(TransaksjonMapping::getDatoEndret))
                .map(mapping -> {
                    try {
                        var vedtak = objectMapper.readValue(mapping.getTransaksjonId(), AlderspensjonVedtakDTO.class);
                        vedtak.setFom(nonNull(vedtak.getFom()) ? vedtak.getFom() : vedtak.getIverksettelsesdato());
                        vedtak.setUttaksgrad(nonNull(vedtak.getUttaksgrad()) ? vedtak.getUttaksgrad() : vedtak.getNyUttaksgrad());
                        return vedtak;
                    } catch (JsonProcessingException e) {
                        log.error("Feil ved deserialisering av transaksjonId", e);
                        return basicAlderspensjonRequestDTO(ident, Set.of(miljoe));
                    }
                })
                .filter(vedtak -> isNull(vedtak.getFom()) || isNull(fomDato) ||
                        vedtak.getFom().isBefore(fomDato))
                .collectList()
                .map(vedtaker -> {
                    if (vedtaker.isEmpty()) {
                        return basicAlderspensjonRequestDTO(ident, Set.of(miljoe));
                    }

                    var datoGradertUttak = new AtomicReference<>(vedtaker.getFirst().getFom());
                    vedtaker.stream()
                            .filter(vedtak -> !vedtak.getUttaksgrad().equals(0) &&
                                    !vedtak.getUttaksgrad().equals(100))
                            .forEach(vedtak -> datoGradertUttak.set(vedtak.getFom()));

                    var datoUttaksgrad = new AtomicInteger(vedtaker.getFirst().getUttaksgrad());
                    vedtaker.forEach(vedtak -> {
                        if (isNull(vedtak.getUttaksgrad())) {
                            vedtak.setUttaksgrad(datoUttaksgrad.get());
                        } else {
                            datoUttaksgrad.set(vedtak.getUttaksgrad());
                        }
                    });

                    vedtaker.getLast().setDatoForrigeGraderteUttak(datoGradertUttak.get());
                    vedtaker.getLast().setHistorikk(vedtaker.subList(0, vedtaker.size() - 1));
                    return vedtaker.getLast();
                });
    }
}
