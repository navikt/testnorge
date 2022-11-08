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

    private static final String TEKNISK_FEIL = "Teknisk feil {} mottatt fra system";
    private static final String TEKNISK_FEIL_SE_LOGG = "Teknisk feil. Se logg! ";
    private static final String ERROR = "error";
    private static final String MESSAGE = "message";
    private static final String MELDING = "melding";
    private static final String DETAILS = "details";
    private static final String FEIL = "Feil= ";

    private final ObjectMapper objectMapper;

    public static String getVarsel(String system) {

        return String.format(VARSEL, system);
    }

    public static String encodeStatus(String toBeEncoded) {
        return Objects.nonNull(toBeEncoded) ?
                toBeEncoded.replaceAll("\\[\\s", "")
                        .replace("[", "")
                        .replace("]", "")
                        .replace(',', ';')
                        .replace(':', '=') : "";
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

        log.error(TEKNISK_FEIL, e.getMessage(), e);
        return new StringBuilder()
                .append(FEIL)
                .append(TEKNISK_FEIL_SE_LOGG)
                .append(encodeStatus(e.getMessage()))
                .toString();
    }

    public String decodeThrowable(Throwable error) {

        StringBuilder builder = new StringBuilder()
                .append(FEIL);

        if (error instanceof WebClientResponseException webClientResponseException) {

            log.info("WebClientResponseException body: {}", webClientResponseException.getResponseBodyAsString());
            if (webClientResponseException.getStatusCode().is4xxClientError()) {
                appendStatusMessage(isNotBlank(webClientResponseException.getResponseBodyAsString()) ?
                        webClientResponseException.getResponseBodyAsString(StandardCharsets.UTF_8) :
                        webClientResponseException.getStatusCode().toString(), builder);

            } else {
                builder.append(TEKNISK_FEIL_SE_LOGG);
                builder.append(encodeStatus(error.getMessage()));
                log.error(TEKNISK_FEIL, error.getMessage(), error);
            }

        } else {
            builder.append(TEKNISK_FEIL_SE_LOGG);
            builder.append(encodeStatus(error.getMessage()));
            log.error(TEKNISK_FEIL, error.getMessage(), error);
        }

        return builder.toString();
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
