package no.nav.dolly.api;

import no.nav.dolly.domain.resultset.kodeverk.KodeverkAdjusted;
import no.nav.dolly.kodeverk.KodeverkConsumer;
import no.nav.dolly.kodeverk.KodeverkMapper;
import no.nav.tjenester.kodeverk.api.v1.Betydning;
import no.nav.tjenester.kodeverk.api.v1.GetKodeverkKoderBetydningerResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class KodeverkControllerTest {

    private static final String STANDARD_KODEVERK_NAME = "name";

    @Mock
    private KodeverkConsumer kodeverkConsumer;

    @Mock
    private KodeverkMapper kodeverkMapper;

    @InjectMocks
    private KodeverkController controller;

    @Test
    public void fetchKodeverkByName_happyPath(){
        Betydning betydning = Mockito.mock(Betydning.class);
        Map<String, List<Betydning>> betydningerMap = new HashMap<>();
        betydningerMap.put("kode", Arrays.asList(betydning));

        GetKodeverkKoderBetydningerResponse getKodeverkBetydningerResMock = Mockito.mock(GetKodeverkKoderBetydningerResponse.class);
        KodeverkAdjusted kodeverk = Mockito.mock(KodeverkAdjusted.class);

        when(kodeverkConsumer.fetchKodeverkByName(STANDARD_KODEVERK_NAME)).thenReturn(getKodeverkBetydningerResMock);
        when(getKodeverkBetydningerResMock.getBetydninger()).thenReturn(betydningerMap);
        when(kodeverkMapper.mapBetydningToAdjustedKodeverk(STANDARD_KODEVERK_NAME, betydningerMap)).thenReturn(kodeverk);

        KodeverkAdjusted kodeverkResponse = controller.fetchKodeverkByName(STANDARD_KODEVERK_NAME);

        assertThat(kodeverkResponse, is(kodeverk));
    }
}