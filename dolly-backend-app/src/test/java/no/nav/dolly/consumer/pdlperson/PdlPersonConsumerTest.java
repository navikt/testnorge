package no.nav.dolly.consumer.pdlperson;

import static no.nav.dolly.domain.resultset.pdlforvalter.TemaGrunnlag.GEN;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.security.sts.StsOidcService;

@RunWith(SpringRunner.class)
@RestClientTest(PdlPersonConsumer.class)
public class PdlPersonConsumerTest {

    private static final String IDENT = "12345678901";

    private MockRestServiceServer server;

    @Autowired
    private RestTemplate restTemplate;

    @MockBean
    private ProvidersProps providersProps;

    @MockBean
    private StsOidcService stsOidcService;

    @Autowired
    private PdlPersonConsumer pdlPersonConsumer;

    @Before
    public void setup() {
        setField(pdlPersonConsumer, "environment", "t1");
        when(providersProps.getPdlPerson()).thenReturn(ProvidersProps.PdlPerson.builder().url("https://pdl-api.nais.preprod.local").build());

        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void getPdlPersonOk() {

        server.expect(requestTo("https://pdl-api.nais.preprod.local/graphql"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Tema", GEN.name()))
                .andRespond(withSuccess());

        ResponseEntity response = pdlPersonConsumer.getPdlPerson(IDENT);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }
}