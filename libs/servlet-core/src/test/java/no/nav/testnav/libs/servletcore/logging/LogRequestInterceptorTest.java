package no.nav.testnav.libs.servletcore.logging;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
class LogRequestInterceptorTest {

    private final LogRequestInterceptor logRequestInterceptor = new LogApiRequestInterceptor();

    @Test
    void shouldNotLogRequest() {

        var request = new MockHttpServletRequest();
        request.setRequestURI("/swagger");

        assertThat(logRequestInterceptor.shouldLogRequest(request)).isFalse();

    }

    @Test
    void shouldLogRequestWithoutBody() {

        var request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/inntektsmelding");

        assertThat(logRequestInterceptor.shouldLogRequest(request)).isTrue();
        assertThat(logRequestInterceptor.shouldLogBodyOfRequest(request)).isFalse();

    }

    @Test
    void shouldLogRequestWithBody() {

        var request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/inntektsmelding");
        request.addHeader(LogRequestInterceptor.HEADER_SHOULD_LOG_BODY, "true");

        assertThat(logRequestInterceptor.shouldLogRequest(request)).isTrue();
        assertThat(logRequestInterceptor.shouldLogBodyOfRequest(request)).isTrue();

    }

    @Test
    void gettingEmptyBodyShouldReturnEmptyString()
            throws Exception {

        var request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/inntektsmelding");
        request.addHeader(LogRequestInterceptor.HEADER_SHOULD_LOG_BODY, "true");

        var body = logRequestInterceptor.getBody(request);
        assertThat(body).isEmpty();

    }

    @Test
    void gettingNonemptyBodyShouldReturnBodyContents()
            throws Exception {

        var request = new MockHttpServletRequest();
        request.setRequestURI("/api/v1/inntektsmelding");
        request.addHeader(LogRequestInterceptor.HEADER_SHOULD_LOG_BODY, "true");
        request.setContent("test".getBytes(StandardCharsets.UTF_8));

        var body = logRequestInterceptor.getBody(request);
        assertThat(body).isEqualTo("test");

    }

}
