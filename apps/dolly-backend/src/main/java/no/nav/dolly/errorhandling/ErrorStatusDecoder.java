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
import java.util.stream.Collectors;

import static no.nav.dolly.util.DollyTextUtil.getInfoTextSystem;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class ErrorStatusDecoder {

    private static final String TEKNISK_FEIL = "Teknisk feil {} mottatt fra system";
    private static final String TEKNISK_FEIL_SE_LOGG = "Teknisk feil. Se logg! ";
    private static final String ERROR = "error";
    private static final String MESSAGE = "message";
    private static final String MELDING = "melding";
    private static final String DETAILS = "details";
    private static final String FEIL = "Feil= ";

    private final ObjectMapper objectMapper;

    public static String getInfoVenter(String system) {

        return encodeStatus(getInfoTextSystem(system));
    }

    public static String encodeStatus(String toBeEncoded) {
        return Objects.nonNull(toBeEncoded) ?
                toBeEncoded.replaceAll("\\[\\s", "")
                        .replace("[", "")
                        .replace("]", "")
                        .replace(',', ';')
                        .replace(':', '=')
                        .replace("\"", "")
                        .replace("$", "§")
                        .replace("\n", ""): "";
    }

    public String getErrorText(HttpStatus errorStatus, String errorMsg) {

        var builder = new StringBuilder()
                .append(FEIL);

        if (isBlank(errorMsg)) {
            builder.append(errorStatus);

        } else if (errorMsg.contains("{")) {
            builder.append(encodeStatus(getStatusMessage(errorMsg)));

        } else {
            builder.append(encodeStatus(errorMsg));
        }

        return builder.toString();
    }

    public String decodeThrowable(Throwable error) {

        StringBuilder builder = new StringBuilder()
                .append(FEIL);

        if (error instanceof WebClientResponseException webClientResponseException) {

            if (webClientResponseException.getStatusCode().is4xxClientError()) {

                builder.append(isNotBlank(webClientResponseException.getResponseBodyAsString()) &&
                        !getStatusMessage(webClientResponseException.getResponseBodyAsString()).equals("null") ?
                        getStatusMessage(webClientResponseException.getResponseBodyAsString(StandardCharsets.UTF_8)) :
                        webClientResponseException.getStatusCode().toString());

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

    public String getStatusMessage(String json) {

        var builder = new StringBuilder();
        if (json.contains("{")) {
            try {
                Map<String, Object> status = objectMapper.readValue(json, Map.class);
                if (status.containsKey(MESSAGE) && isNotBlank((String) status.get(MESSAGE))) {
                    builder.append("message=").append(encodeStatus((String) status.get(MESSAGE))).append("; ");
                } else if (status.containsKey(ERROR) && isNotBlank((String) status.get(ERROR))) {
                    builder.append("error=").append(status.get(ERROR)).append("; ");
                } else if (status.containsKey(MELDING) && isNotBlank((String) status.get(MELDING))) {
                    builder.append(encodeStatus((String) status.get(MELDING)));
                } else if (status.containsKey(DETAILS) && status.get(DETAILS) instanceof List) {
                    StringBuilder meldinger = new StringBuilder("=");
                    List<Map<String, String>> details = (List) status.get(DETAILS);
                    details.forEach(entry ->
                            entry.forEach((key, value) ->
                                    meldinger.append(' ').append(key).append("= ").append(value)));
                    builder.append("details=").append(encodeStatus(meldinger.toString()));
                } else {
                    builder.append(status.entrySet().stream()
                            .map(entry -> entry.getKey() + ": " + entry.getValue())
                            .collect(Collectors.joining(",")));
                }

            } catch (IOException ioe) {
                builder.append(encodeStatus(ioe.getMessage()));
            }

        } else {
            builder.append(encodeStatus(json));
        }

        return builder.toString();
    }
}
