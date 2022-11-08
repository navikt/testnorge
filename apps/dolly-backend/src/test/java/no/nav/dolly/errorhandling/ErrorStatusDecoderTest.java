package no.nav.dolly.errorhandling;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.charset.Charset;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

@ExtendWith(MockitoExtension.class)
class ErrorStatusDecoderTest {

    @InjectMocks
    private ErrorStatusDecoder errorStatusDecoder;

    @Test
    void getWebClientStatus_responsebodyEmpty() {

        var headers = new HttpHeaders();
        headers.put(HttpHeaders.CONTENT_TYPE, List.of(MediaType.APPLICATION_JSON_VALUE));

        var throwable = new WebClientResponseException(400, "Bad request", headers, new byte[]{}, Charset.defaultCharset());
        var target = errorStatusDecoder.decodeThrowable(throwable);

        assertThat(target, containsString("BAD_REQUEST"));
    }
}