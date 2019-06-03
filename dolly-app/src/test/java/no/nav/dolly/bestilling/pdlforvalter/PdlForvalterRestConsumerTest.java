package no.nav.dolly.bestilling.pdlforvalter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import no.nav.dolly.domain.resultset.pdlforvalter.doedsbo.PdlKontaktinformasjonForDoedsbo;
import no.nav.dolly.domain.resultset.pdlforvalter.utenlandsid.PdlUtenlandskIdentifikasjonsnummer;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.sts.StsOidcService;

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

        when(providersProps.getPdlForvalter()).thenReturn(ProvidersProps.PdlForvalter.builder().url(PDL_URL).build());
    }

    @Test
    public void postKontaktinformasjonForDoedsbo_OK() {

        pdlForvalterRestConsumer.postKontaktinformasjonForDoedsbo(PdlKontaktinformasjonForDoedsbo.builder().build(), IDENT);

        verify(providersProps).getPdlForvalter();
        verify(stsOidcService).getIdToken(anyString());
        verify(restTemplate).exchange(any(RequestEntity.class), eq(JsonNode.class));
    }

    @Test
    public void postUtenlandskIdentifikasjonsnummer_OK() {

        pdlForvalterRestConsumer.postUtenlandskIdentifikasjonsnummer(PdlUtenlandskIdentifikasjonsnummer.builder().build(), IDENT);

        verify(providersProps).getPdlForvalter();
        verify(stsOidcService).getIdToken(anyString());
        verify(restTemplate).exchange(any(RequestEntity.class), eq(JsonNode.class));
    }

    @Test
    public void deleteIdent() {

        pdlForvalterRestConsumer.deleteIdent(IDENT);

        verify(providersProps).getPdlForvalter();
        verify(stsOidcService).getIdToken(anyString());
        verify(restTemplate).exchange(any(RequestEntity.class), eq(JsonNode.class));
    }
}