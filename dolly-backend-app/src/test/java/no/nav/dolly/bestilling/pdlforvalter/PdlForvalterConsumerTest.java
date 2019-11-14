package no.nav.dolly.bestilling.pdlforvalter;

import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_PERSON_IDENT;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import no.nav.dolly.bestilling.pdlforvalter.domain.PdlNavn;
import no.nav.dolly.bestilling.pdlforvalter.domain.PdlOpprettPerson;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.PdlKontaktinformasjonForDoedsbo;
import no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet.PdlFalskIdentitet;
import no.nav.dolly.domain.resultset.pdlforvalter.utenlandsid.PdlUtenlandskIdentifikasjonsnummer;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.security.sts.StsOidcService;

@RunWith(SpringRunner.class)
@RestClientTest(PdlForvalterConsumer.class)
public class PdlForvalterConsumerTest {

    private static final String IDENT = "11111111111";
    private static final String PDL_URL = "http://pdl.nav.no";

    private MockRestServiceServer server;

    @Autowired
    private RestTemplate restTemplate;

    @MockBean
    private ProvidersProps providersProps;

    @MockBean
    private StsOidcService stsOidcService;

    @Autowired
    private PdlForvalterConsumer pdlForvalterConsumer;

    @Before
    public void setup() {

        ReflectionTestUtils.setField(pdlForvalterConsumer, "environment", "u2");
        when(providersProps.getPdlForvalter()).thenReturn(ProvidersProps.PdlForvalter.builder().url(PDL_URL).build());

        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void postKontaktinformasjonForDoedsbo_OK() {

        server.expect(requestTo("http://pdl.nav.no/api/v1/bestilling/kontaktinformasjonfordoedsbo"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HEADER_NAV_PERSON_IDENT, IDENT))
                .andRespond(withSuccess());

        pdlForvalterConsumer.postKontaktinformasjonForDoedsbo(PdlKontaktinformasjonForDoedsbo.builder().build(), IDENT);

        verify(providersProps).getPdlForvalter();
        verify(stsOidcService, times(2)).getIdToken(anyString());
    }

    @Test
    public void postUtenlandskIdentifikasjonsnummer_OK() {

        server.expect(requestTo("http://pdl.nav.no/api/v1/bestilling/utenlandsidentifikasjonsnummer"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        pdlForvalterConsumer.postUtenlandskIdentifikasjonsnummer(PdlUtenlandskIdentifikasjonsnummer.builder().build(), IDENT);

        verify(providersProps).getPdlForvalter();
        verify(stsOidcService, times(2)).getIdToken(anyString());
    }

    @Test
    public void postFalskIdenitet_OK() {

        server.expect(requestTo("http://pdl.nav.no/api/v1/bestilling/falskidentitet"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HEADER_NAV_PERSON_IDENT, IDENT))
                .andRespond(withSuccess());

        pdlForvalterConsumer.postFalskIdentitet(PdlFalskIdentitet.builder().build(), IDENT);

        verify(providersProps).getPdlForvalter();
        verify(stsOidcService, times(2)).getIdToken(anyString());
    }

    @Test
    public void deleteIdent() {

        server.expect(requestTo("http://pdl.nav.no/api/v1/ident"))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(header(HEADER_NAV_PERSON_IDENT, IDENT))
                .andRespond(withSuccess());

        pdlForvalterConsumer.deleteIdent(IDENT);

        verify(providersProps).getPdlForvalter();
        verify(stsOidcService, times(2)).getIdToken(anyString());
    }

    @Test
    public void opprettPerson() {

        server.expect(requestTo("http://pdl.nav.no/api/v1/bestilling/opprettperson"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HEADER_NAV_PERSON_IDENT, IDENT))
                .andRespond(withSuccess());

        pdlForvalterConsumer.postOpprettPerson(PdlOpprettPerson.builder().opprettetIdent(IDENT).build(), IDENT);

        verify(providersProps).getPdlForvalter();
        verify(stsOidcService, times(2)).getIdToken(anyString());
    }

    @Test
    public void leggTillNavn() {

        server.expect(requestTo("http://pdl.nav.no/api/v1/bestilling/navn"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HEADER_NAV_PERSON_IDENT, IDENT))
                .andRespond(withSuccess());

        pdlForvalterConsumer.postNavn(PdlNavn.builder().build(), IDENT);
    }

    @Test
    public void getPersonstatus() {

        server.expect(requestTo("http://pdl.nav.no/api/v1/personstatus"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HEADER_NAV_PERSON_IDENT, IDENT))
                .andRespond(withSuccess());

        pdlForvalterConsumer.getPersonstatus(IDENT);
    }
}