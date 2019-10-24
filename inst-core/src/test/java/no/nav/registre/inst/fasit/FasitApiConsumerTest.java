package no.nav.registre.inst.fasit;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import no.nav.registre.inst.properties.ProvidersProps;

@RunWith(MockitoJUnitRunner.class)
public class FasitApiConsumerTest {

    private static final String BASE_URL = "baseUrl";
    private static final String ALIAS = "alias";
    private static final String TYPE = "type";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ProvidersProps providersProps;

    @Mock
    private ResponseEntity responseEntity;

    @Mock
    private HttpClientErrorException httpClientErrorException;

    @InjectMocks
    private FasitApiConsumer fasitApiConsumer;

    @Test
    public void fetchResources_OK() {

        when(providersProps.getFasit()).thenReturn(ProvidersProps.Fasit.builder().url(BASE_URL).build());
        when(restTemplate.getForEntity(anyString(),
                eq(FasitResourceWithUnmappedProperties[].class))).thenReturn(responseEntity);

        fasitApiConsumer.fetchResources(ALIAS, TYPE);

        verify(restTemplate).getForEntity(anyString(), eq(FasitResourceWithUnmappedProperties[].class));
    }

    @Test(expected = FasitException.class)
    public void fetchResources_FailToReadFromFasit() {
        when(providersProps.getFasit()).thenReturn(ProvidersProps.Fasit.builder().url(BASE_URL).build());
        when(restTemplate.getForEntity(anyString(),
                eq(FasitResourceWithUnmappedProperties[].class))).thenThrow(httpClientErrorException);
        when(httpClientErrorException.getResponseBodyAsString()).thenReturn("Error message");

        fasitApiConsumer.fetchResources(ALIAS, TYPE);

        verify(restTemplate).getForEntity(anyString(), eq(FasitResourceWithUnmappedProperties[].class));
    }
}