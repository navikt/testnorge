package no.nav.identpool.navnepool;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import no.nav.identpool.navnepool.domain.Navn;

@RunWith(Parameterized.class)
public class NavnepoolServiceValideringTest {

    private static String fornavn1 = "fornavn1";
    private static String etternavn1 = "etternavn1";
    @Parameter
    public String fornavn;
    @Parameter(1)
    public String etternavn;
    @Parameter(2)
    public Boolean expectedValid;
    private List<String> validFornavn = Arrays.asList(fornavn1, "fornavn2");
    private List<String> validEtternavn = Arrays.asList(etternavn1, "etternavn2", "etternavn3");
    private NavnepoolService navnepoolService = new NavnepoolService(validFornavn, validEtternavn);

    @Parameters
    public static Collection<Object[]> params() {
        return Arrays.asList(new Object[][] {
                { fornavn1, etternavn1, true },
                { null, etternavn1, true },
                { fornavn1, null, true },
                { null, null, false },
                { "tullenavn", "tullenavn", false }
        });
    }

    /**
     * Testscenario: Metoden isValid skal returnere true kun DERSOM minst en av fornavn og etternavn er oppgitt,
     * og deres verdier er gyldige (dvs. er å finne i listen med godkjente fornavn og etternavn).
     */
    @Test
    public void testValidering() {
        Boolean actualValid = navnepoolService.isValid(new Navn(fornavn, etternavn));
        assertEquals(expectedValid, actualValid);
    }
}