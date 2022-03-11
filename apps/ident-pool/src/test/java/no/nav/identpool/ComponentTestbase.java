package no.nav.identpool;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.identpool.domain.Ident;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Kjoenn;
import no.nav.identpool.domain.Rekvireringsstatus;
import no.nav.identpool.repository.IdentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.time.LocalDate;

import static no.nav.identpool.util.PersonidentUtil.isSyntetisk;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {ComponentTestConfig.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc(addFilters = false)
public abstract class ComponentTestbase {
    protected static final String IDENT_V1_BASEURL = "/api/v1/identifikator";
    protected static final String FINNESHOSSKATT_V1_BASEURL = "/api/v1/finneshosskatt";

    @Autowired
    protected IdentRepository identRepository;

    @Autowired
    protected TestRestTemplate testRestTemplate;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected TpsfConsumer tpsfConsumer;
    private HttpEntityBuilder httpEntityBuilder;

    @BeforeEach
    public void initEntityBuilder() {
        httpEntityBuilder = new HttpEntityBuilder();
    }

    protected Ident createIdentEntity(Identtype identtype, String ident, Rekvireringsstatus rekvireringsstatus, int day) {
        return Ident.builder()
                .identtype(identtype)
                .personidentifikator(ident)
                .rekvireringsstatus(rekvireringsstatus)
                .finnesHosSkatt(false)
                .kjoenn(Kjoenn.MANN)
                .foedselsdato(LocalDate.of(1980, 10, day))
                .syntetisk(isSyntetisk(ident))
                .build();
    }

    protected <T> ResponseEntity<T> doGetRequest(URI uri, HttpEntity httpEntity, Class<T> responseEntity) {
        return doRequest(uri, HttpMethod.GET, httpEntity, responseEntity);
    }

    protected HttpEntity createBodyEntity(String body) {
        return httpEntityBuilder.withBody(body).build();
    }

    private <T> ResponseEntity<T> doRequest(URI uri, HttpMethod method, HttpEntity httpEntity, Class<T> responseEntity) {
        return testRestTemplate.exchange(uri, method, httpEntity, responseEntity);
    }

    private class HttpEntityBuilder {
        private Object body;
        private MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

        HttpEntityBuilder withBody(Object body) {
            this.body = body;
            return this;
        }

        HttpEntity build() {
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