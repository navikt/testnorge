package no.nav.registre.orkestratoren.service;

import static org.junit.Assert.*;

import java.time.LocalDate;
import org.junit.Test;

public class FnrUtilityTest {

    @Test
    public void shouldGetFoedselsdatoFromFnr() {
        String fnr1 = "14041212345";
        String fnr2 = "14041254321";
        assertEquals(LocalDate.of(1912, 4, 14), FnrUtility.getFoedselsdatoFraFnr(fnr1));
        assertEquals(LocalDate.of(2012, 4, 14), FnrUtility.getFoedselsdatoFraFnr(fnr2));
    }
}