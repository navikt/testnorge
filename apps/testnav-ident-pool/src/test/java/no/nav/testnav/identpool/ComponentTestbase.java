package no.nav.testnav.identpool;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.testnav.identpool.consumers.TpsMessagingConsumer;
import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Kjoenn;
import no.nav.testnav.identpool.domain.Rekvireringsstatus;
import no.nav.testnav.identpool.repository.IdentRepository;
import no.nav.dolly.libs.test.DollySpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.time.LocalDate;

import static no.nav.testnav.identpool.util.PersonidentUtil.isSyntetisk;

@DollySpringBootTest(classes = {ComponentTestConfig.class})
@AutoConfigureMockMvc(addFilters = false)
public abstract class ComponentTestbase {

    protected static final String IDENT_V1_BASEURL = "/api/v1/identifikator";

    @Autowired
    protected IdentRepository identRepository;

    @Autowired
    protected TestRestTemplate testRestTemplate;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockitoBean
    protected TpsMessagingConsumer tpsMessagingConsumer;

    private HttpEntityBuilder httpEntityBuilder;

    @BeforeEach
    void initEntityBuilder() {
        httpEntityBuilder = new HttpEntityBuilder();
    }

    protected Ident createIdentEntity(Identtype identtype, String ident, Rekvireringsstatus rekvireringsstatus, int day) {
        return Ident.builder()
                .identtype(identtype)
                .personidentifikator(ident)
                .rekvireringsstatus(rekvireringsstatus)
                .kjoenn(Kjoenn.MANN)
                .foedselsdato(LocalDate.of(1980, 10, day))
                .syntetisk(isSyntetisk(ident))
                .build();
    }

    protected <T> ResponseEntity<T> doGetRequest(URI uri, HttpEntity<?> httpEntity, Class<T> responseEntity) {
        return doRequest(uri, HttpMethod.GET, httpEntity, responseEntity);
    }

    protected HttpEntity<?> createBodyEntity(String body) {
        return httpEntityBuilder.withBody(body).build();
    }

    private <T> ResponseEntity<T> doRequest(URI uri, HttpMethod method, HttpEntity<?> httpEntity, Class<T> responseEntity) {
        return testRestTemplate.exchange(uri, method, httpEntity, responseEntity);
    }

    private static class HttpEntityBuilder {
        private Object body;
        private final MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

        HttpEntityBuilder withBody(Object body) {
            this.body = body;
            return this;
        }

        HttpEntity<?> build() {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");

            if (!headers.isEmpty()) {
                httpHeaders.addAll(headers);
            }

            if (body != null) {
                return new HttpEntity<>(body, httpHeaders);
            }
            return new HttpEntity<>(httpHeaders);
        }
    }

}