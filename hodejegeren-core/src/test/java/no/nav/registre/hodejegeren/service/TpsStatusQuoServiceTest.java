package no.nav.registre.hodejegeren.service;

import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.DATO_DO;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.FNR_RELASJON;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.STATSBORGERSKAP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import no.nav.registre.hodejegeren.consumer.TpsfConsumer;

@RunWith(MockitoJUnitRunner.class)
public class TpsStatusQuoServiceTest {

    @InjectMocks
    private TpsStatusQuoService tpsStatusQuoService;

    @Mock
    private TpsfConsumer tpsfConsumer;

    private String fnr = "12345678901";
    private String routineName = "FS03-FDNUMMER-KERNINFO-O";
    private URL jsonContent = Resources.getResource("FS03-FDNUMMER-KERNINFO-O.json");
    private String environment = "t1";

    /**
     * Testscenario: HVIS hentStatusQuo blir kalt med et simpelt feltnavn, så skal den returnere servicerutine-feltets verdi i en
     * map.
     */
    @Test
    public void shouldGetStatusQuoForFeltnavn() throws IOException {
        var jsonNode = new ObjectMapper().readTree(jsonContent);

        List<String> feltNavn = new ArrayList<>();
        feltNavn.add(STATSBORGERSKAP);

        when(tpsfConsumer.getTpsServiceRoutine(any(), any(), any(), any())).thenReturn(jsonNode);

        var statusQuoValues = tpsStatusQuoService.hentStatusQuo(routineName, feltNavn, environment, fnr);

        assertEquals(1, statusQuoValues.size());
        assertEquals("NOR", statusQuoValues.get(STATSBORGERSKAP));
    }

    /**
     * Testscenario: HVIS hentStatusQuo blir kalt med feltnavn som er avhengig av et annet felt, skal man få tilbake riktig felt gitt
     * korrekt spørring
     */
    @Test
    public void shouldGetStatusQuoForFeltnavnRelasjon() throws IOException {
        var jsonNode = new ObjectMapper().readTree(Resources.getResource("FS02-FDNUMMER-PERSRELA-O.json"));

        List<String> feltNavn = new ArrayList<>();
        feltNavn.add(FNR_RELASJON);

        when(tpsfConsumer.getTpsServiceRoutine(any(), any(), any(), any())).thenReturn(jsonNode);

        var statusQuoValues = tpsStatusQuoService.hentStatusQuo(routineName, feltNavn, environment, fnr);

        assertEquals(1, statusQuoValues.size());
        assertEquals("01065500791", statusQuoValues.get(FNR_RELASJON));

    }

    /**
     * Testscenario: HVIS hentStatusQuo blir kalt med feltnavn på formen "felt1/felt2/.../feltX, så skal den returnere
     * servicerutine-feltX sin verdi i en map.
     */
    @Test
    public void shouldGetStatusQuoForFeltnavnPath() throws IOException {
        var jsonNode = new ObjectMapper().readTree(jsonContent);

        List<String> feltNavn = new ArrayList<>();
        feltNavn.add("$..bostedsAdresse.fullBostedsAdresse.adrSaksbehandler");
        feltNavn.add("$..bostedsAdresse.fullBostedsAdresse.offAdresse.husnr");

        when(tpsfConsumer.getTpsServiceRoutine(any(), any(), any(), any())).thenReturn(jsonNode);

        var statusQuoValues = tpsStatusQuoService.hentStatusQuo(routineName, feltNavn, environment, fnr);

        assertEquals(2, statusQuoValues.size());
        assertEquals("AJOURHD", statusQuoValues.get("$..bostedsAdresse.fullBostedsAdresse.adrSaksbehandler"));
        assertEquals("1289", statusQuoValues.get("$..bostedsAdresse.fullBostedsAdresse.offAdresse.husnr"));
    }

    /**
     * Testscenario: HVIS getInfoOnRoutineName blir kalt skal metoden hente en rutine, og sørge for at innholdet rutinen caches.
     * Hvis en cache ikke finnes, skal denne opprettes. Cachen skal resettes når hentStatusQuo kalles.
     */
    @Test
    public void shouldUpdateCacheWithRoutine() throws IOException {
        final String aksjonsKode = "B0";
        final String environment = "Q11";

        var jsonNode = new ObjectMapper().readTree(jsonContent);

        when(tpsfConsumer.getTpsServiceRoutine(any(), any(), any(), any())).thenReturn(jsonNode);

        assertNull(tpsStatusQuoService.getTpsServiceRoutineCache());

        tpsStatusQuoService.getInfoOnRoutineName(routineName, aksjonsKode, environment, fnr);

        var tpsServiceRoutineCache = tpsStatusQuoService.getTpsServiceRoutineCache();

        assertNotNull(tpsServiceRoutineCache);
        assertTrue(tpsServiceRoutineCache.containsKey(routineName));
        assertEquals(jsonNode, tpsServiceRoutineCache.get(routineName));

        tpsStatusQuoService.hentStatusQuo("FS03-FDNUMMER-PERSDATA-O", Collections.singletonList(DATO_DO), environment, fnr);
        assertNotEquals(tpsServiceRoutineCache, tpsStatusQuoService.getTpsServiceRoutineCache());
    }
}
