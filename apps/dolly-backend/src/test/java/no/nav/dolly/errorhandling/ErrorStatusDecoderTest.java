package no.nav.dolly.errorhandling;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ErrorStatusDecoderTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ErrorStatusDecoder errorStatusDecoder;

    @Test
    void getWebClientStatus_responseBodyEmpty() {

        var headers = new HttpHeaders();
        headers.put(HttpHeaders.CONTENT_TYPE, List.of(MediaType.APPLICATION_JSON_VALUE));

        var throwable = new WebClientResponseException(400, "Bad request", headers, new byte[]{}, Charset.defaultCharset());
        var target = errorStatusDecoder.decodeThrowable(throwable);

        assertThat(target, containsString("BAD_REQUEST"));
    }

    @Test
    void getWebClientStatus_responseBodyNullMelding() {

        var headers = new HttpHeaders();
        headers.put(HttpHeaders.CONTENT_TYPE, List.of(MediaType.APPLICATION_JSON_VALUE));

        var throwable = new WebClientResponseException(400, "Bad request", headers, "{\"melding\":\"null\"}".getBytes(StandardCharsets.UTF_8),
                Charset.defaultCharset());

        when(objectMapper.readValue(anyString(), eq(Map.class))).thenReturn(Map.of("melding", "null"));
        var target = errorStatusDecoder.decodeThrowable(throwable);

        assertThat(target, containsString("BAD_REQUEST"));
    }
}