package no.nav.dolly.provider;

import static no.nav.dolly.config.SecurityTestConfig.OPEN_AM_ISSUER_URL;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_ID;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import no.nav.dolly.common.TestdataFactory;
import no.nav.freg.security.test.oidc.tools.JwtClaimsBuilder;
import no.nav.freg.security.test.oidc.tools.OidcTestService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public abstract class RestTestBase {

    protected static final String NAV_IDENT = "NAVIDENT";
    private static final String DEFAULT_CALL_ID = "CallId_1337";
    private static final String DEFAULT_CONSUMER_ID = "TestCase";
    private static final String ERR_MSG_KEY = "message";

    @Autowired
    protected TestdataFactory dataFactory;

    @Autowired
    protected OidcTestService oidcTestService;

    @Autowired
    protected TestRestTemplate restTestTemplate;

    @BeforeEach
    void init() {
        dataFactory.clearDatabase();
    }

    protected EndpointRequestBuilder sendRequest() {
        return send(null);
    }

    protected EndpointRequestBuilder sendRequest(Object requestBody) {
        return send(requestBody);
    }

    protected String getErrMsg(LinkedHashMap resp) {
        return (String) resp.get(ERR_MSG_KEY);
    }

    protected String createOidcToken() {
        return oidcTestService.createOidc(
                new JwtClaimsBuilder()
                        .subject(NAV_IDENT)
                        .audience("aud")
                        .expiry(LocalDateTime.now().plusMinutes(10))
                        .validFrom(LocalDateTime.now().minusMinutes(5))
                        .azp("azp")
                        .issuer(OPEN_AM_ISSUER_URL)
                        .build());
    }

    private EndpointRequestBuilder send(Object requestBody) {
        return new EndpointRequestBuilder(requestBody)
                .withHeader(HEADER_NAV_CALL_ID, DEFAULT_CALL_ID)
                .withHeader(HEADER_NAV_CONSUMER_ID, DEFAULT_CONSUMER_ID)
                .withHeader(HttpHeaders.AUTHORIZATION, "Bearer " + createOidcToken());
    }


    protected class EndpointRequestBuilder {
        private Map<String, String> requestHeaders = new HashMap<>();
        private Object requestBody;

        private EndpointRequestBuilder(Object requestBody) {
            this.requestBody = requestBody;
        }

        public EndpointRequestBuilder withHeader(String headerName, String headerValue) {
            requestHeaders.put(headerName, headerValue);
            return this;
        }

        public EndpointRequestBuilder withoutHeader(String headerName) {
            requestHeaders.remove(headerName);
            return this;
        }

        public ResponsePredicateBuilder to(HttpMethod httpMethod, String uri) {
            return new ResponsePredicateBuilder(requestHeaders, requestBody, httpMethod, uri);
        }
    }

    protected class ResponsePredicateBuilder {
        private Map<String, String> requestHeaders;
        private Object requestBody;
        private HttpMethod method;
        private String uri;

        private ResponsePredicateBuilder(Map<String, String> requestHeaders, Object requestBody, HttpMethod httpMethod, String uri) {
            this.requestHeaders = requestHeaders;
            this.requestBody = requestBody;
            this.method = httpMethod;
            this.uri = uri;
        }

        public <T> T andExpect(HttpStatus expectedHttpStatus, Class<T> expectedResponseType) {
            ResponseEntity<T> responseEntity = andExpectResponseEntityFor(expectedResponseType);
            assertThat(responseEntity.getStatusCode(), is(expectedHttpStatus));
            return responseEntity.getBody();
        }

        public <T> List<T> andExpectList(HttpStatus expectedHttpStatus, ParameterizedTypeReference<List<T>> expectedResponseType) {
            ResponseEntity<List<T>> responseEntity = andExpectResponseEntityWithListFor(expectedResponseType);
            assertThat(responseEntity.getStatusCode(), is(expectedHttpStatus));
            return responseEntity.getBody();
        }

        private <T> ResponseEntity<T> andExpectResponseEntityFor(Class<T> expectedResponseType) {
            HttpHeaders headers = new HttpHeaders();
            requestHeaders.forEach(headers::add);

            return restTestTemplate.exchange(uri, method, new HttpEntity<>(requestBody, headers), expectedResponseType);
        }

        private <T> ResponseEntity<List<T>> andExpectResponseEntityWithListFor(ParameterizedTypeReference<List<T>> expectedResponseType) {
            HttpHeaders headers = new HttpHeaders();
            requestHeaders.forEach(headers::add);

            return restTestTemplate.exchange(uri, method, new HttpEntity<>(requestBody, headers), expectedResponseType);
        }

        private <T> ResponseEntity<Map<T, T>> andExpectResponseEntityWithMapFor(ParameterizedTypeReference<Map<T, T>> expectedResponseType) {
            HttpHeaders headers = new HttpHeaders();
            requestHeaders.forEach(headers::add);

            return restTestTemplate.exchange(uri, method, new HttpEntity<>(requestBody, headers), expectedResponseType);
        }

    }
}
