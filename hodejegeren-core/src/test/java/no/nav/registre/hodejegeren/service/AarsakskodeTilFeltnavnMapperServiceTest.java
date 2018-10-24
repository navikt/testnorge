package no.nav.registre.hodejegeren.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AarsakskodeTilFeltnavnMapperServiceTest {
    @InjectMocks
    private AarsakskodeTilFeltnavnMapperService aarsakskodeTilFeltnavnMapperService;

    @Test
    public void mapFunksjonTilFeltnavn() {
        AarsakskoderTrans1 aarsakskoderTrans1 = AarsakskoderTrans1.NAVNEENDRING_FOERSTE;

        List<String> feltnavn = aarsakskodeTilFeltnavnMapperService.mapAarsakskodeTilFeltnavn(aarsakskoderTrans1);

        assertEquals(2, feltnavn.size());
        assertTrue(feltnavn.contains("datoDo"));
        assertTrue(feltnavn.contains("statsborger"));
    }
}
