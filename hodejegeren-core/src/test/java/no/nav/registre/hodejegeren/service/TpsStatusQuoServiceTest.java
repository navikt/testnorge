package no.nav.registre.hodejegeren.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.registre.hodejegeren.consumer.TpsfConsumer;

@RunWith(MockitoJUnitRunner.class)
public class TpsStatusQuoServiceTest {

    @InjectMocks
    private TpsStatusQuoService tpsStatusQuoService;

    @Mock
    private TpsfConsumer tpsfConsumer;

    private String aksjonsKode = "A0";
    private String environment = "Q11";
    private String fnr = "12345678901";
    private String routineName = "FS03-FDNUMMER-PERSDATA-O";
    private String jsonContent = "{\"response\":{\"data1\":{\"personStatus\":\"Død                             Fnr\",\"kortnavn\":\"HATT VAKKER\",\"datoInnvandret\":\"1953-05-12\",\"kommunenr\":\"\",\"epost\":{\"epostTidspunktReg\":\"\",\"epostSystem\":\"\",\"epostAdresse\":\"\",\"epostSaksbehandler\":\"\"},\"tlfPrivat\":{\"tlfSystem\":\"\",\"tlfTidspunktReg\":\"\",\"tlfNummer\":\"\",\"tlfSaksbehandler\":\"\"},\"tidligereKommunenr\":\"0219\",\"tknr\":\"0219\",\"tlfJobb\":{\"tlfSystem\":\"\",\"tlfTidspunktReg\":\"\",\"tlfNummer\":\"\",\"tlfSaksbehandler\":\"\"},\"bolignr\":\"\",\"datoDo\":\"2018-10-15\",\"datoUtvandret\":\"\",\"etternavn\":\"HATT\",\"postnr\":\"\",\"datoUmyndiggjort\":\"\",\"boPoststed\":\"\",\"utvandretTil\":\"\",\"boAdresse2\":\"\",\"mellomnavn\":\"\",\"innvandretFra\":\"ST.KITTS OG NEVIS\",\"giroInfo\":{\"giroNummer\":\"\",\"giroTidspunktReg\":\"\",\"giroSystem\":\"\",\"giroSaksbehandler\":\"\"},\"datoFlyttet\":\"2018-10-15\",\"postAdresse2\":\"\",\"postAdresse1\":\"\",\"datoSivilstand\":\"2018-10-15\",\"fnr\":12055310651,\"fornavn\":\"VAKKER\",\"sivilstand\":\"Gift\",\"postAdresse3\":\"\",\"datoStatsborger\":\"1953-05-12\",\"spesregType\":\"\",\"boAdresse1\":\"\",\"statsborger\":\"NORGE\",\"tlfMobil\":{\"tlfSystem\":\"\",\"tlfTidspunktReg\":\"\",\"tlfNummer\":\"\",\"tlfSaksbehandler\":\"\"}},\"status\":{\"kode\":\"00\",\"melding\":\" \",\"utfyllendeMelding\":\" \"},\"antallTotalt\":null}}";

    @Test
    public void getStatusQuo() throws IOException {

        JsonNode jsonNode = new ObjectMapper().readTree(jsonContent);

        List<String> feltNavn = new ArrayList<>();
        feltNavn.add("datoDo");

        when(tpsfConsumer.getTpsServiceRoutine(any(), any())).thenReturn(jsonNode);

        Map<String, String> statusQuoValues = tpsStatusQuoService.getStatusQuo(feltNavn, aksjonsKode, environment, fnr);

        assertEquals(1, statusQuoValues.size());
        assertEquals("2018-10-15", statusQuoValues.get("datoDo"));
    }

    @Test
    public void getInfoOnRoutineName() throws IOException {
        JsonNode jsonNode = new ObjectMapper().readTree(jsonContent);

        when(tpsfConsumer.getTpsServiceRoutine(any(), any())).thenReturn(jsonNode);

        assertTrue(tpsStatusQuoService.getRootCache() == null);

        tpsStatusQuoService.getInfoOnRoutineName(routineName, aksjonsKode, environment, fnr);

        assertTrue(tpsStatusQuoService.getRootCache() != null);
        assertTrue(tpsStatusQuoService.getRootCache().containsKey(routineName));
        assertEquals(jsonNode, tpsStatusQuoService.getRootCache().get(routineName));
    }

    @Test
    public void getInfoHelper() throws IOException {
        List<String> feltnavn = new ArrayList<>();
        feltnavn.add("sivilstand");

        Map<String, Object> tpsRequestParameters = new HashMap<>();
        tpsRequestParameters.put("aksjonsKode", aksjonsKode);
        tpsRequestParameters.put("environment", environment);
        tpsRequestParameters.put("fnr", fnr);

        tpsStatusQuoService.getInfoHelper(routineName, aksjonsKode, environment, fnr);

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(tpsfConsumer).getTpsServiceRoutine(eq(routineName), captor.capture());
        Map<String, Object> actualRequestParams = captor.getValue();

        assertEquals(aksjonsKode, actualRequestParams.get("aksjonsKode"));
        assertEquals(environment, actualRequestParams.get("environment"));
        assertEquals(fnr, actualRequestParams.get("fnr"));
    }
}
