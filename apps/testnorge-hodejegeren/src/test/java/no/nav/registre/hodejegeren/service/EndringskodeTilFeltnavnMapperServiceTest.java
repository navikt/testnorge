package no.nav.registre.hodejegeren.service;

import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.DATO_DO;
import static no.nav.registre.hodejegeren.service.EndringskodeTilFeltnavnMapperService.STATSBORGER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class EndringskodeTilFeltnavnMapperServiceTest {

    @InjectMocks
    private EndringskodeTilFeltnavnMapperService endringskodeTilFeltnavnMapperService;

    @Mock
    private TpsStatusQuoService tpsStatusQuoService;

    private String fnr = "12345678901";
    private String environment = "t1";
    private String routineName = "FS03-FDNUMMER-PERSDATA-O";

    /**
     * Testscenario: HVIS et kall gjøres til getStatusQuoFraAarsakskode med en aarsakskode, skal riktig servicerutine og feltnavn
     * bli sendt til hentStatusQuo()
     */
    @Test
    public void shouldFindFeltnavnAndServiceRoutineFromAarsakskode() throws IOException {
        var endringskode = Endringskoder.NAVNEENDRING_FOERSTE.getEndringskode();

        endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(endringskode, environment, fnr);

        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(tpsStatusQuoService).hentStatusQuo(eq(routineName), captor.capture(), eq(environment), eq(fnr));
        List<String> actualRequestParams = captor.getValue();

        assertEquals(2, actualRequestParams.size());
        assertTrue(actualRequestParams.contains(DATO_DO));
        assertTrue(actualRequestParams.contains(STATSBORGER));
    }
}
