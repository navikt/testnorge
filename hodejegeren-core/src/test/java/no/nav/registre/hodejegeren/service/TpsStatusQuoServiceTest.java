package no.nav.registre.hodejegeren.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import no.nav.registre.hodejegeren.consumer.TpsfConsumer;
import no.nav.registre.hodejegeren.exception.ManglendeInfoITpsException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TpsStatusQuoServiceTest {

    @InjectMocks
    private TpsStatusQuoService tpsStatusQuoService;

    @Mock
    private TpsfConsumer tpsfConsumer;

    private String aksjonsKode = "A0";
    private String environment = "Q11";
    private String fnr = "12345678901";
    private String routineName = "FS03-FDNUMMER-KERNINFO-O";
    private URL jsonContent = Resources.getResource("FS03-FDNUMMER-KERNINFO-O.json");

    /**
     * Testscenario: HVIS getStatusQuo blir kalt med et simpelt feltnavn, så skal den returnere servicerutine-feltets verdi i en
     * map.
     */
    @Test
    public void shouldGetStatusQuoForFeltnavn() throws IOException {
        JsonNode jsonNode = new ObjectMapper().readTree(jsonContent);

        List<String> feltNavn = new ArrayList<>();
        feltNavn.add("statsborgerskap");

        when(tpsfConsumer.getTpsServiceRoutine(any(), any())).thenReturn(jsonNode);

        Map<String, String> statusQuoValues = tpsStatusQuoService.getStatusQuo(routineName, feltNavn, aksjonsKode, environment, fnr);

        assertEquals(1, statusQuoValues.size());
        assertEquals("NOR", statusQuoValues.get("statsborgerskap"));
    }

    /**
     * Testscenario: HVIS getStatusQuo blir kalt med feltnavn som er avhengig av et annet felt, skal man få tilbake riktig felt gitt
     * korrekt spørring
     */
    @Test
    public void shouldGetStatusQuoForFeltnavnRelasjon() throws IOException {
        JsonNode jsonNode = new ObjectMapper().readTree(Resources.getResource("FS02-FDNUMMER-PERSRELA-O.json"));

        List<String> feltNavn = new ArrayList<>();
        feltNavn.add("$..relasjon[?(@.typeRelasjon=='EKTE')].fnrRelasjon");

        when(tpsfConsumer.getTpsServiceRoutine(any(), any())).thenReturn(jsonNode);

        Map<String, String> statusQuoValues = tpsStatusQuoService.getStatusQuo(routineName, feltNavn, aksjonsKode, environment, fnr);

        assertEquals(1, statusQuoValues.size());
        assertEquals("01065500791", statusQuoValues.get("$..relasjon[?(@.typeRelasjon=='EKTE')].fnrRelasjon"));

    }

    /**
     * Testscenario: HVIS getStatusQuo blir kalt med feltnavn på formen "felt1/felt2/.../feltX, så skal den returnere
     * servicerutine-feltX sin verdi i en map.
     */
    @Test
    public void shouldGetStatusQuoForFeltnavnPath() throws IOException {
        JsonNode jsonNode = new ObjectMapper().readTree(jsonContent);

        List<String> feltNavn = new ArrayList<>();
        feltNavn.add("$..bostedsAdresse.fullBostedsAdresse.adrSaksbehandler");
        feltNavn.add("$..bostedsAdresse.fullBostedsAdresse.offAdresse.husnr");

        when(tpsfConsumer.getTpsServiceRoutine(any(), any())).thenReturn(jsonNode);

        Map<String, String> statusQuoValues = tpsStatusQuoService.getStatusQuo(routineName, feltNavn, aksjonsKode, environment, fnr);

        assertEquals(2, statusQuoValues.size());
        assertEquals("AJOURHD", statusQuoValues.get("$..bostedsAdresse.fullBostedsAdresse.adrSaksbehandler"));
        assertEquals("1289", statusQuoValues.get("$..bostedsAdresse.fullBostedsAdresse.offAdresse.husnr"));
    }

    /**
     * Testscenario: HVIS getInfoOnRoutineName blir kalt skal metoden hente en rutine, og sørge for at innholdet rutinen caches.
     * Hvis en cache ikke finnes, skal denne opprettes. Cachen skal resettes når getStatusQuo kalles.
     */
    @Test
    public void shouldUpdateCacheWithRoutine() throws IOException {
        JsonNode jsonNode = new ObjectMapper().readTree(jsonContent);

        when(tpsfConsumer.getTpsServiceRoutine(any(), any())).thenReturn(jsonNode);

        assertNull(tpsStatusQuoService.getTpsServiceRoutineCache());

        tpsStatusQuoService.getInfoOnRoutineName(routineName, aksjonsKode, environment, fnr);

        assertNotNull(tpsStatusQuoService.getTpsServiceRoutineCache());
        assertTrue(tpsStatusQuoService.getTpsServiceRoutineCache().containsKey(routineName));
        assertEquals(jsonNode, tpsStatusQuoService.getTpsServiceRoutineCache().get(routineName));

        Map<String, JsonNode> cache = tpsStatusQuoService.getTpsServiceRoutineCache();
        tpsStatusQuoService.getStatusQuo("FS03-FDNUMMER-PERSDATA-O", Arrays.asList("datoDo"), aksjonsKode, environment, fnr);
        assertNotEquals(cache, tpsStatusQuoService.getTpsServiceRoutineCache());
    }

    @Test
    public void shouldHandleTpsRequestParameters() throws IOException {
        tpsStatusQuoService.getInfoHelper(routineName, aksjonsKode, environment, fnr);

        System.out.print("hello");

        ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(tpsfConsumer).getTpsServiceRoutine(eq(routineName), captor.capture());
        Map<String, Object> actualRequestParams = captor.getValue();

        assertEquals(aksjonsKode, actualRequestParams.get("aksjonsKode"));
        assertEquals(environment, actualRequestParams.get("environment"));
        assertEquals(fnr, actualRequestParams.get("fnr"));
    }
}
