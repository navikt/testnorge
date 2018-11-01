package no.nav.registre.hodejegeren.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;

import java.io.IOException;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EndringskodeTilFeltnavnMapperServiceTest {
    
    @InjectMocks
    private EndringskodeTilFeltnavnMapperService endringskodeTilFeltnavnMapperService;
    
    @Mock
    private TpsStatusQuoService tpsStatusQuoService;
    
    private String aksjonsKode = "A0";
    private String environment = "Q11";
    private String fnr = "12345678901";
    private String routineName = "FS03-FDNUMMER-PERSDATA-O";
    
    /**
     * Testscenario: HVIS et kall gjøres til getStatusQuoFraAarsakskode med en aarsakskode, skal riktig servicerutine og feltnavn
     * bli sendt til getStatusQuo()
     */
    @Test
    public void shouldFindFeltnavnAndServiceRoutineFromAarsakskode() throws IOException {
        endringskodeTilFeltnavnMapperService.getStatusQuoFraAarsakskode(Endringskoder.NAVNEENDRING_FOERSTE, aksjonsKode, environment, fnr);
        
        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(tpsStatusQuoService).getStatusQuo(eq(routineName), captor.capture(), eq(aksjonsKode), eq(environment), eq(fnr));
        List<String> actualRequestParams = captor.getValue();
        
        assertEquals(2, actualRequestParams.size());
        assertTrue(actualRequestParams.contains("datoDo"));
        assertTrue(actualRequestParams.contains("statsborger"));
    }
}
