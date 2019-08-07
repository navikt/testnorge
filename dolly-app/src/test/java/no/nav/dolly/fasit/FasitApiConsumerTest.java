package no.nav.dolly.fasit;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import no.nav.dolly.consumer.fasit.FasitApiConsumer;
import no.nav.dolly.consumer.fasit.FasitResourceWithUnmappedProperties;
import no.nav.dolly.exceptions.FasitException;
import no.nav.dolly.properties.ProvidersProps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

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

    @InjectMocks
    private FasitApiConsumer fasitApiConsumer;

    @Mock
    private HttpClientErrorException httpClientErrorException;

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
        when(httpClientErrorException.getStatusCode()).thenReturn(HttpStatus.BAD_REQUEST);
        when(httpClientErrorException.getResponseBodyAsString()).thenReturn("Error message");

        fasitApiConsumer.fetchResources(ALIAS, TYPE);

        verify(restTemplate).getForEntity(anyString(), eq(FasitResourceWithUnmappedProperties[].class));
    }
}