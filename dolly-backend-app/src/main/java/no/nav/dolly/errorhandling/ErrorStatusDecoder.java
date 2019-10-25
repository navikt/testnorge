package no.nav.dolly.errorhandling;

import java.io.IOException;
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

    private final ObjectMapper objectMapper;

    public String getErrorText(HttpStatus errorStatus, String errorMsg) {

        StringBuilder builder = new StringBuilder()
                .append("Feil: ");

        if (errorMsg.contains("{")) {

            try {
                builder.append(encodeErrorStatus((String) objectMapper.readValue(errorMsg, Map.class).get("message")));

            } catch (IOException e) {

                builder.append(errorStatus.value())
                        .append(" (")
                        .append(errorStatus.getReasonPhrase())
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
                .append("Feil: ");

        if (e instanceof HttpClientErrorException) {

            if (((HttpClientErrorException) e).getResponseBodyAsString().contains("{")) {
                try {
                    builder.append(encodeErrorStatus((String) objectMapper.readValue(((HttpClientErrorException) e).getResponseBodyAsString(), Map.class).get("message")));

                } catch (IOException ioe) {
                    builder.append(e.getMessage());
                }

            } else {

                builder.append(encodeErrorStatus(((HttpClientErrorException) e).getResponseBodyAsString()));
            }

        } else {

            builder.append(" Teknisk feil. Se logg!");
            log.error("Teknisk feil {} mottatt fra system", e.getMessage(), e);
        }

        return builder.toString();
    }

    private static String encodeErrorStatus(String toBeEncoded) {
        return toBeEncoded.replaceAll(",", "&").replaceAll(":", "=");
    }
}
