package no.nav.registre.aareg.consumer.rs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import no.nav.registre.aareg.security.sts.StsOidcService;

@ExtendWith(MockitoExtension.class)
public class AaregRestConsumerTest {

    private static final String IDENT = "1111111111";
    private static final String ENV = "t0";

    @Mock
    private AaregArbeidsforholdFasitConsumer aaregArbeidsforholdFasitConsumer;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private StsOidcService stsOidcService;

    @InjectMocks
    private AaregRestConsumer aaregRestConsumer;

    @Test
    public void hentArbeidsforhold() {
        when(aaregArbeidsforholdFasitConsumer.getUrlForEnv(ENV)).thenReturn("baseurl");

        aaregRestConsumer.hentArbeidsforhold(IDENT, ENV);

        verify(stsOidcService, times(2)).getIdToken(ENV);
        verify(aaregArbeidsforholdFasitConsumer).getUrlForEnv(ENV);
        verify(restTemplate).exchange(any(RequestEntity.class), eq(new ParameterizedTypeReference<List<JsonNode>>() {
        }));
    }
}