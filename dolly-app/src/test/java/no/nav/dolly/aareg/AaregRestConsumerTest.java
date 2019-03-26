package no.nav.dolly.aareg;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import no.nav.dolly.sts.StsOidcService;

@RunWith(MockitoJUnitRunner.class)
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
    public void readArbeidsforhold() {

        when(aaregArbeidsforholdFasitConsumer.getUrlForEnv(ENV)).thenReturn("baseurl");

        aaregRestConsumer.readArbeidsforhold(IDENT, ENV);

        verify(stsOidcService, times(2)).getIdToken(ENV);
        verify(aaregArbeidsforholdFasitConsumer).getUrlForEnv(ENV);
        verify(restTemplate).exchange(any(RequestEntity.class), eq(Object[].class));
    }
}