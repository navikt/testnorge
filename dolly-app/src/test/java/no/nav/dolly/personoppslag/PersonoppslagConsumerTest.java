package no.nav.dolly.personoppslag;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import no.nav.dolly.consumer.personoppslag.PersonoppslagConsumer;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.security.sts.StsOidcService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class PersonoppslagConsumerTest {

    private static final String IDENT = "12345678901";
    private static final String URL = "personOppslagUrl";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ProvidersProps providersProps;

    @Mock
    private StsOidcService stsOidcService;

    @InjectMocks
    private PersonoppslagConsumer personoppslagConsumer;

    @Test
    public void fetchPerson_OK() {

        when(providersProps.getPersonOppslag()).thenReturn(ProvidersProps.PersonOppslag.builder().url(URL).build());

        personoppslagConsumer.fetchPerson(IDENT);

        verify(providersProps).getPersonOppslag();
        verify(stsOidcService, times(2)).getIdToken(anyString());
        verify(restTemplate).exchange(any(RequestEntity.class), eq(JsonNode.class));
    }
}