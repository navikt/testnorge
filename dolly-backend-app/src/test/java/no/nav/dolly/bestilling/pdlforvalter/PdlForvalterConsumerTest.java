package no.nav.dolly.bestilling.pdlforvalter;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

    @Autowired
    private MockRestServiceServer server;

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
    }

    @Test
    public void postKontaktinformasjonForDoedsbo_OK() {

        server.expect(requestTo("http://pdl.nav.no/api/v1/bestilling/kontaktinformasjonfordoedsbo"))
                .andExpect(method(HttpMethod.POST))
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
                .andRespond(withSuccess());

        pdlForvalterConsumer.postFalskIdentitet(PdlFalskIdentitet.builder().build(), IDENT);

        verify(providersProps).getPdlForvalter();
        verify(stsOidcService, times(2)).getIdToken(anyString());
    }

    @Test
    public void deleteIdent() {

        server.expect(requestTo("http://pdl.nav.no/api/v1/ident"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess());

        pdlForvalterConsumer.deleteIdent(IDENT);

        verify(providersProps).getPdlForvalter();
        verify(stsOidcService, times(2)).getIdToken(anyString());
    }
}