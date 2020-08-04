package no.nav.dolly.errorhandling;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ErrorStatusDecoder {

    private static final String ERROR = "error";
    private static final String MESSAGE = "message";
    private static final String DETAILS = "details";

    private final ObjectMapper objectMapper;

    public String getErrorText(HttpStatus errorStatus, String errorMsg) {

        StringBuilder builder = new StringBuilder()
                .append("Feil= ");

        if (errorMsg.contains("{")) {

            try {
                builder.append(encodeErrorStatus((String) objectMapper.readValue(errorMsg, Map.class).get("message")));

            } catch (IOException e) {

                builder.append(errorStatus.value())
                        .append(" (")
                        .append(encodeErrorStatus(errorStatus.getReasonPhrase()))
                        .append(')');

                log.warn("Parsing av melding '{}' feilet", errorMsg, e);
            }

        } else {

            builder.append(encodeErrorStatus(errorMsg));
        }

        return builder.toString();
    }

    public String decodeRuntimeException(RuntimeException e) {

        StringBuilder builder = new StringBuilder()
                .append("Feil= ");

        if (e instanceof HttpClientErrorException) {

            if (((HttpClientErrorException) e).getResponseBodyAsString().contains("{")) {
                try {
                    Map<String, Object> status = objectMapper.readValue(((HttpClientErrorException) e).getResponseBodyAsString(), Map.class);
                    if (status.containsKey(ERROR) && isNotBlank((String) status.get(ERROR))) {
                        builder.append("error=").append(status.get(ERROR)).append(';');
                    }
                    if (status.containsKey(MESSAGE) && isNotBlank((String) status.get(MESSAGE))) {
                        builder.append("message=").append(encodeErrorStatus((String) status.get(MESSAGE))).append(';');
                    }
                    if (status.containsKey(DETAILS) && status.get(DETAILS) instanceof List) {
                        StringBuilder meldinger = new StringBuilder("=");
                        List<Map<String, String>> details = (List) status.get(DETAILS);
                        details.forEach(entry ->
                                entry.forEach((key, value) ->
                                        meldinger.append(' ').append(key).append("= ").append(value)));
                        builder.append("details=").append(encodeErrorStatus(meldinger.toString()));
                    }

                } catch (IOException ioe) {
                    builder.append(encodeErrorStatus(ioe.getMessage()));
                }

            } else {
                    builder.append(encodeErrorStatus(((HttpClientErrorException) e).getResponseBodyAsString()));
            }

        } else {

            builder.append("Teknisk feil. Se logg!");
            log.error("Teknisk feil {} mottatt fra system", e.getMessage(), e);
        }

        return builder.toString();
    }

    public static String encodeErrorStatus(String toBeEncoded) {
        return toBeEncoded
                .replace("[\"", "")
                .replace("\"]", "")
                .replaceAll(",", ";")
                .replaceAll(":", "=");
    }
}
