package no.nav.dolly.errorhandling;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class ErrorStatusDecoder {

    private static final String VARSEL = "Varsel: Sending til %s er ikke utført, da personen ennå ikke finnes i PDL. " +
            "Forsøk gjenopprett for å fikse dette!";

    private static final String ERROR = "error";
    private static final String MESSAGE = "message";
    private static final String MELDING = "melding";
    private static final String DETAILS = "details";
    private static final String FEIL = "Feil= ";

    private final ObjectMapper objectMapper;

    public static String getVarsel(String system) {

        return String.format(VARSEL, system);
    }

    public String getErrorText(HttpStatus errorStatus, String errorMsg) {

        StringBuilder builder = new StringBuilder()
                .append(FEIL);

        if (errorMsg.contains("{")) {

            try {
                builder.append(encodeStatus((String) objectMapper.readValue(errorMsg, Map.class).get("message")));

            } catch (IOException e) {

                builder.append(errorStatus.value())
                        .append(" (")
                        .append(encodeStatus(errorStatus.getReasonPhrase()))
                        .append(')');

                log.warn("Parsing av melding '{}' feilet", errorMsg, e);
            }

        } else {

            builder.append(encodeStatus(errorMsg));
        }

        return builder.toString();
    }

    public String decodeException(Exception e) {

        log.error("Teknisk feil {} mottatt fra system", e.getMessage(), e);
        return new StringBuilder()
                .append(FEIL)
                .append("Teknisk feil. Se logg! ")
                .append(encodeStatus(e.getMessage()))
                .toString();
    }

    public String decodeRuntimeException(RuntimeException error) {

        StringBuilder builder = new StringBuilder()
                .append(FEIL);

        if (error instanceof WebClientResponseException webClientResponseException) {

            if ( webClientResponseException.getStatusCode().is4xxClientError()) {
                appendStatusMessage(webClientResponseException.getResponseBodyAsString().isEmpty() ?
                        webClientResponseException.getStatusCode().toString() :
                        webClientResponseException.getResponseBodyAsString(StandardCharsets.UTF_8), builder);

            } else {
                builder.append("Teknisk feil. Se logg!");
                log.error("Teknisk feil {} mottatt fra system", error.getMessage(), error);
            }

        } else {
            builder.append("Teknisk feil. Se logg! ");
            builder.append(encodeStatus(error.getMessage()));
            log.error("Teknisk feil {} mottatt fra system", error.getMessage(), error);
        }

        return builder.toString();
    }

    public static String encodeStatus(String toBeEncoded) {
        return Objects.nonNull(toBeEncoded) ?
                toBeEncoded.replaceAll("\\[\\s", "")
                        .replace("[", "")
                        .replace("]", "")
                        .replace(',', ';')
                        .replace(':', '=') : "";
    }

    private void appendStatusMessage(String responseBody, StringBuilder builder) {

        if (responseBody.contains("{")) {
            try {
                Map<String, Object> status = objectMapper.readValue(responseBody, Map.class);
                if (status.containsKey(ERROR) && isNotBlank((String) status.get(ERROR))) {
                    builder.append("error=").append(status.get(ERROR)).append("; ");
                }
                if (status.containsKey(MESSAGE) && isNotBlank((String) status.get(MESSAGE))) {
                    builder.append("message=").append(encodeStatus((String) status.get(MESSAGE))).append("; ");
                }
                if (status.containsKey(MESSAGE) && isNotBlank((String) status.get(MESSAGE))) {
                    builder.append("message=").append(encodeStatus((String) status.get(MESSAGE))).append("; ");
                }
                if (status.containsKey(MELDING) && isNotBlank((String) status.get(MELDING))) {
                    builder.append(encodeStatus((String) status.get(MELDING)));
                }
                if (status.containsKey(DETAILS) && status.get(DETAILS) instanceof List) {
                    StringBuilder meldinger = new StringBuilder("=");
                    List<Map<String, String>> details = (List) status.get(DETAILS);
                    details.forEach(entry ->
                            entry.forEach((key, value) ->
                                    meldinger.append(' ').append(key).append("= ").append(value)));
                    builder.append("details=").append(encodeStatus(meldinger.toString()));
                }

            } catch (IOException ioe) {
                builder.append(encodeStatus(ioe.getMessage()));
            }

        } else {
            builder.append(encodeStatus(responseBody));
        }
    }
}
