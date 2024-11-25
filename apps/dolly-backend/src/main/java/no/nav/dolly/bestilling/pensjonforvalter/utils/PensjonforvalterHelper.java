package no.nav.dolly.bestilling.pensjonforvalter.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.domain.jpa.TransaksjonMapping;
import no.nav.dolly.domain.resultset.SystemTyper;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.service.TransaksjonMappingService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static no.nav.dolly.errorhandling.ErrorStatusDecoder.encodeStatus;
import static org.apache.poi.util.StringUtil.isNotBlank;

@Slf4j
@Service
@NoArgsConstructor
public class PensjonforvalterHelper {

    private TransaksjonMappingService transaksjonMappingService;
    private ObjectMapper objectMapper;
    private ErrorStatusDecoder errorStatusDecoder;

    public PensjonforvalterHelper(TransaksjonMappingService transaksjonMappingService, ObjectMapper objectMapper, ErrorStatusDecoder errorStatusDecoder) {
        this.transaksjonMappingService = transaksjonMappingService;
        this.objectMapper = objectMapper;
        this.errorStatusDecoder = errorStatusDecoder;
    }

    @SuppressWarnings("java:S3740")
    public void saveAPTransaksjonId(String ident, String miljoe, Long bestillingId,
                                    SystemTyper type, AtomicReference vedtak) {

        log.info("Lagrer transaksjon for {} i {} ", ident, miljoe);
        transaksjonMappingService.delete(ident, miljoe, type.name());

        transaksjonMappingService.save(
                TransaksjonMapping.builder()
                        .ident(ident)
                        .bestillingId(bestillingId)
                        .transaksjonId(toJson(vedtak.get()))
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
}
