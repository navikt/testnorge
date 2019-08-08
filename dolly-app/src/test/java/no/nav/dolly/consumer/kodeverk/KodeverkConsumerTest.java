package no.nav.dolly.consumer.kodeverk;

import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_ID;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import no.nav.dolly.consumer.kodeverk.KodeverkConsumer;
import no.nav.dolly.exceptions.KodeverkException;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.tjenester.kodeverk.api.v1.GetKodeverkKoderBetydningerResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RunWith(MockitoJUnitRunner.class)
public class KodeverkConsumerTest {

    private static final String KODEVERK_URL = "url";
    private static final String KODEVERK_NAVN = "name";
    private static final String KODEVERK_QUERY_PARAM = "?ekskluderUgyldige=true&spraak=nb";
    @Mock
    ProvidersProps providersProps;
    @Mock
    RestTemplate restTemplate;
    @InjectMocks
    KodeverkConsumer kodeverkConsumer;
    private GetKodeverkKoderBetydningerResponse standardKodeverkResponse = new GetKodeverkKoderBetydningerResponse();
    private ResponseEntity standardReponseEntity = new ResponseEntity(standardKodeverkResponse, HttpStatus.OK);

    @Before
    public void setup() {
        ProvidersProps.Kodeverk kodeverk = new ProvidersProps.Kodeverk();
        kodeverk.setUrl(KODEVERK_URL);
        when(providersProps.getKodeverk()).thenReturn(kodeverk);
    }

    @Test
    public void fetchKodeverkByName_UrlErSattBasertPaaKodeverksnavnOgHeaderBlirSattTilDollySittAppNavn() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(GetKodeverkKoderBetydningerResponse.class))).thenReturn(standardReponseEntity);

        kodeverkConsumer.fetchKodeverkByName(KODEVERK_NAVN);

        ArgumentCaptor<String> capUrl = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity> capEntity = ArgumentCaptor.forClass(HttpEntity.class);

        verify(restTemplate).exchange(capUrl.capture(), any(HttpMethod.class), capEntity.capture(), eq(GetKodeverkKoderBetydningerResponse.class));

        assertThat(capUrl.getValue(), is(KODEVERK_URL + "/api/v1/kodeverk/name/koder/betydninger" + KODEVERK_QUERY_PARAM));
        assertThat(capEntity.getValue().getHeaders().getFirst(HEADER_NAV_CONSUMER_ID), is("srvdolly"));
        assertThat(capEntity.getValue().getHeaders().getFirst(HEADER_NAV_CALL_ID), is(notNullValue()));
    }

    @Test(expected = KodeverkException.class)
    public void fetchKodeverkByName_kasterKodeverkexceptionHvisResttemplateGetKalletFeilerMotKodeverk() {
        HttpClientErrorException errorEx = new HttpClientErrorException(HttpStatus.NOT_FOUND, "msg");
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(GetKodeverkKoderBetydningerResponse.class))).thenThrow(errorEx);

        kodeverkConsumer.fetchKodeverkByName(KODEVERK_NAVN);
    }

    @Test(expected = KodeverkException.class)
    public void fetchKodeverkByName_kasterKodeverkExceptionOgGirStatusKodeNotFound() {
        HttpClientErrorException errorEx = new HttpClientErrorException(HttpStatus.NOT_FOUND);
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(GetKodeverkKoderBetydningerResponse.class))).thenThrow(errorEx);

        kodeverkConsumer.fetchKodeverkByName(KODEVERK_NAVN);
    }
}