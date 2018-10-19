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
public class FunksjonTilFeltnavnMapperServiceTest {
    @InjectMocks
    private FunksjonTilFeltnavnMapperService funksjonTilFeltnavnMapperService;

    @Mock
    private TpsStatusQuoService tpsStatusQuoService;

    @Test
    public void mapFunksjonTilFeltnavn() throws IOException {
        Funksjoner funksjon = Funksjoner.NAVNEENDRING_FOERSTE;
        String aksjonsKode = "A0";
        String environment = "Q11";
        String fnr = "12345678901";

        funksjonTilFeltnavnMapperService.mapFunksjonTilFeltnavn(funksjon, aksjonsKode, environment, fnr);

        ArgumentCaptor<List<String>> captor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(tpsStatusQuoService).getStatusQuo(captor.capture(), eq(aksjonsKode), eq(environment), eq(fnr));
        List<String> actualRequestParams = captor.getValue();

        assertEquals(2, actualRequestParams.size());
        assertTrue(actualRequestParams.contains("datoDo"));
        assertTrue(actualRequestParams.contains("statsborger"));
    }
}
