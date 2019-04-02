package no.nav.dolly.norg2;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.properties.ProvidersProps;

@RunWith(MockitoJUnitRunner.class)
public class Norg2ConsumerTest {

    private static final String ENHET_NR = "1";
    private static final String BASE_URL = "baseUrl";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    ProvidersProps providersProps;

    @InjectMocks
    private Norg2Consumer norg2Consumer;

    @Mock
    private ResponseEntity responseEntity;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void fetchEnhetByEnhetNr_OK() {

        when(providersProps.getNorg2()).thenReturn(ProvidersProps.Norg2.builder().url(BASE_URL).build());
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(Norg2EnhetResponse.class))).thenReturn(responseEntity);

        norg2Consumer.fetchEnhetByEnhetNr(ENHET_NR);

        verify(providersProps).getNorg2();
        verify(restTemplate).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(Norg2EnhetResponse.class));
    }

    @Test
    public void fetchEnhetByEnhetNr_EnhetNotFound() {

        when(providersProps.getNorg2()).thenReturn(ProvidersProps.Norg2.builder().url(BASE_URL).build());
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET),
                any(HttpEntity.class), eq(Norg2EnhetResponse.class))).thenThrow(HttpClientErrorException.class);

        expectedException.expect(NotFoundException.class);
        expectedException.expectMessage("Enhet med nummeret '1' eksisterer ikke");

        norg2Consumer.fetchEnhetByEnhetNr(ENHET_NR);
    }
}