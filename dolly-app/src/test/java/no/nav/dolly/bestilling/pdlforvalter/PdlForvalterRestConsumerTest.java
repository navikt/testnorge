package no.nav.dolly.bestilling.pdlforvalter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.PdlKontaktinformasjonForDoedsbo;
import no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet.PdlFalskIdentitet;
import no.nav.dolly.domain.resultset.pdlforvalter.utenlandsid.PdlUtenlandskIdentifikasjonsnummer;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.security.sts.StsOidcService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@RunWith(MockitoJUnitRunner.class)
public class PdlForvalterRestConsumerTest {

    private static final String IDENT = "11111111111";
    private static final String PDL_URL = "http://pdl.nav.no";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ProvidersProps providersProps;

    @Mock
    private StsOidcService stsOidcService;

    @InjectMocks
    private PdlForvalterRestConsumer pdlForvalterRestConsumer;

    @Before
    public void setup() {

        ReflectionTestUtils.setField(pdlForvalterRestConsumer, "environment", "u2");
        when(providersProps.getPdlForvalter()).thenReturn(ProvidersProps.PdlForvalter.builder().url(PDL_URL).build());
    }

    @Test
    public void postKontaktinformasjonForDoedsbo_OK() {

        pdlForvalterRestConsumer.postKontaktinformasjonForDoedsbo(PdlKontaktinformasjonForDoedsbo.builder().build(), IDENT);

        verify(providersProps).getPdlForvalter();
        verify(stsOidcService, times(2)).getIdToken(anyString());
        verify(restTemplate).exchange(any(URI.class), eq(HttpMethod.POST), any(HttpEntity.class), eq(JsonNode.class));
    }

    @Test
    public void postUtenlandskIdentifikasjonsnummer_OK() {

        pdlForvalterRestConsumer.postUtenlandskIdentifikasjonsnummer(PdlUtenlandskIdentifikasjonsnummer.builder().build(), IDENT);

        verify(providersProps).getPdlForvalter();
        verify(stsOidcService, times(2)).getIdToken(anyString());
        verify(restTemplate).exchange(any(URI.class), eq(HttpMethod.POST), any(HttpEntity.class), eq(JsonNode.class));
    }

    @Test
    public void postFalskIdenitet_OK() {

        pdlForvalterRestConsumer.postFalskIdentitet(PdlFalskIdentitet.builder().build(), IDENT);

        verify(providersProps).getPdlForvalter();
        verify(stsOidcService, times(2)).getIdToken(anyString());
        verify(restTemplate).exchange(any(URI.class), eq(HttpMethod.POST), any(HttpEntity.class), eq(JsonNode.class));
    }

    @Test
    public void deleteIdent() {

        pdlForvalterRestConsumer.deleteIdent(IDENT);

        verify(providersProps).getPdlForvalter();
        verify(stsOidcService, times(2)).getIdToken(anyString());
        verify(restTemplate).exchange(any(URI.class), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(JsonNode.class));
    }
}