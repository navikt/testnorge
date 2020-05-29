package no.nav.dolly.provider.api;

import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import no.nav.dolly.consumer.kodeverk.KodeverkConsumer;
import no.nav.dolly.consumer.kodeverk.KodeverkMapper;
import no.nav.dolly.domain.resultset.kodeverk.KodeverkAdjusted;
import no.nav.tjenester.kodeverk.api.v1.Betydning;
import no.nav.tjenester.kodeverk.api.v1.GetKodeverkKoderBetydningerResponse;

@RunWith(MockitoJUnitRunner.class)
public class OppslagControllerTest {

    private static final String STANDARD_KODEVERK_NAME = "name";

    @Mock
    private KodeverkConsumer kodeverkConsumer;

    @Mock
    private KodeverkMapper kodeverkMapper;

    @InjectMocks
    private OppslagController oppslagController;

    @Mock
    private Betydning betydning;

    @Mock
    private KodeverkAdjusted kodeverkAdjusted;

    @Mock
    private GetKodeverkKoderBetydningerResponse getKodeverkKoderBetydningerResponse;

    @Test
    public void fetchKodeverkByName_happyPath() {
        Map<String, List<Betydning>> betydningerMap = new HashMap<>();
        betydningerMap.put("kode", singletonList(betydning));

        when(kodeverkConsumer.fetchKodeverkByName(STANDARD_KODEVERK_NAME)).thenReturn(getKodeverkKoderBetydningerResponse);
        when(getKodeverkKoderBetydningerResponse.getBetydninger()).thenReturn(betydningerMap);
        when(kodeverkMapper.mapBetydningToAdjustedKodeverk(STANDARD_KODEVERK_NAME, betydningerMap)).thenReturn(kodeverkAdjusted);

        KodeverkAdjusted kodeverkResponse = oppslagController.fetchKodeverkByName(STANDARD_KODEVERK_NAME);

        assertThat(kodeverkResponse, is(kodeverkAdjusted));
    }
}