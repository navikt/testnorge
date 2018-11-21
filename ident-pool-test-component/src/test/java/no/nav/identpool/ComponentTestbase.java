package no.nav.identpool;

import static no.nav.identpool.SecurityTestConfig.NAV_STS_ISSUER_URL;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;

import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Kjoenn;
import no.nav.identpool.domain.Rekvireringsstatus;
import no.nav.identpool.repository.IdentEntity;
import no.nav.identpool.rs.v1.support.IdentRequest;
import org.jose4j.jwt.JwtClaims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import no.nav.freg.security.test.oidc.tools.JwtClaimsBuilder;
import no.nav.freg.security.test.oidc.tools.OidcTestService;
import no.nav.identpool.repository.IdentRepository;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { ComponentTestConfig.class }, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class ComponentTestbase {
    protected static final String IDENT_V1_BASEURL = "/api/v1/identifikator";
    protected static final String FINNESHOSSKATT_V1_BASEURL = "/api/v1/finneshosskatt";

    @Autowired
    protected IdentRepository identRepository;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private OidcTestService oidcTestService;

    private HttpEntityBuilder httpEntityBuilder;

    @BeforeEach
    public void initEntityBuilder() {
        httpEntityBuilder = new HttpEntityBuilder();
    }

    protected IdentEntity createIdentEntity(Identtype identtype, String ident, Rekvireringsstatus rekvireringsstatus, int day) {
        return IdentEntity.builder()
                .identtype(identtype)
                .personidentifikator(ident)
                .rekvireringsstatus(rekvireringsstatus)
                .finnesHosSkatt(false)
                .kjoenn(Kjoenn.MANN)
                .foedselsdato(LocalDate.of(1980, 10, day))
                .build();
    }

    protected <T> ResponseEntity<T> doPostRequest(URI uri, HttpEntity httpEntity, Class<T> responseEntity) {
        return doRequest(uri, HttpMethod.POST, httpEntity, responseEntity);
    }

    protected <T> ResponseEntity<T> doGetRequest(URI uri, HttpEntity httpEntity, Class<T> responseEntity) {
        return doRequest(uri, HttpMethod.GET, httpEntity, responseEntity);
    }

    protected HttpEntity createBodyEntity(String body) {
        return httpEntityBuilder.withBody(body).build();
    }

    protected HttpEntity createBodyEntity(IdentRequest request, boolean withOidc) {
        return withOidc ? httpEntityBuilder.withOidcToken().withBody(request).build() : httpEntityBuilder.withBody(request).build();
    }

    protected HttpEntity createHeaderEntity(LinkedMultiValueMap<String, String> headers) {
        return httpEntityBuilder.withHeaders(headers).build();
    }

    private <T> ResponseEntity<T> doRequest(URI uri, HttpMethod method, HttpEntity httpEntity, Class<T> responseEntity) {
        return testRestTemplate.exchange(uri, method, httpEntity, responseEntity);
    }

    private JwtClaims getJwtClaims() {
        return new JwtClaimsBuilder()
                .subject("sub")
                .audience("aud")
                .expiry(LocalDateTime.now().plusMinutes(10))
                .validFrom(LocalDateTime.now().minusMinutes(5))
                .azp("azp")
                .issuer(NAV_STS_ISSUER_URL)
                .build();
    }

    private class HttpEntityBuilder {
        private Object body;
        private MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        private boolean addOidcToken = false;

        HttpEntityBuilder withBody(Object body) {
            this.body = body;
            return this;
        }

        HttpEntityBuilder withHeaders(MultiValueMap<String, String> headers) {
            this.headers = headers;
            return this;
        }

        HttpEntityBuilder withOidcToken() {
            addOidcToken = true;
            return this;
        }

        HttpEntity build() {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");

            if (!headers.isEmpty()) {
                httpHeaders.addAll(headers);
            }

            if (addOidcToken) {
                httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + oidcTestService.createOidc(getJwtClaims()));
            }

            if (body != null) {
                return new HttpEntity<>(body, httpHeaders);
            }
            return new HttpEntity<>(httpHeaders);
        }

    }
}