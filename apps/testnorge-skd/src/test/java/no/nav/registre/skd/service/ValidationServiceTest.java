package no.nav.registre.skd.service;

import static no.nav.registre.skd.testutils.Utils.testLoggingInClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.Test;

import java.util.ArrayList;

import no.nav.registre.skd.domain.Endringskoder;
import no.nav.registre.skd.skdmelding.RsMeldingstype;
import no.nav.registre.skd.skdmelding.RsMeldingstype1Felter;

public class ValidationServiceTest {

    private final ValidationService validator = new ValidationService();

    @Test
    public void shouldLogValidationOfInvalidMessage() {
        ListAppender<ILoggingEvent> listAppender = testLoggingInClass(ValidationService.class);

        var melding1 = RsMeldingstype1Felter.builder().antallBarn("2421234").build();
        var melding2 = RsMeldingstype1Felter.builder().antallBarn("4").build();
        final ArrayList<RsMeldingstype> liste = new ArrayList<>();
        liste.add(melding1);
        liste.add(melding2);
        validator.logAndRemoveInvalidMessages(liste, Endringskoder.INNVANDRING);

        assertEquals(1, listAppender.list.size());
        assertTrue(listAppender.list.get(0).toString().contains("Valideringsfeil for melding med aarsakskode null."
                + " size must be between 0 and 2 for variabelen antallBarn=2421234."));
    }

    /**
     * Testscenario: Valideringen av syntetiske meldinger fra TPS Synt skal ignorere tomme meldinger (null) i responsen.
     */
    @Test
    public void shouldRemoveNullMessages() {
        RsMeldingstype1Felter melding1 = null;
        RsMeldingstype1Felter melding3 = null;
        RsMeldingstype1Felter melding2 = RsMeldingstype1Felter.builder().antallBarn("4").build();

        final ArrayList<RsMeldingstype> liste = new ArrayList<>();
        liste.add(melding1);
        liste.add(melding2);
        liste.add(melding3);
        validator.logAndRemoveInvalidMessages(liste, Endringskoder.INNVANDRING);

        assertEquals(1, liste.size());
        assertFalse(liste.contains(null));
    }
}