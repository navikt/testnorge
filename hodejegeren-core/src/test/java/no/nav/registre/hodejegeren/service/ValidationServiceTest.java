package no.nav.registre.hodejegeren.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype1Felter;

public class ValidationServiceTest {
    
    ValidationService validator = new ValidationService();
    
    @Test
    public void shouldValidate() {
        Logger logger = (Logger) LoggerFactory.getLogger(ValidationService.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
        
        RsMeldingstype1Felter melding1 = RsMeldingstype1Felter.builder().antallBarn("2421234").build();
        RsMeldingstype1Felter melding2 = RsMeldingstype1Felter.builder().antallBarn("4").build();
        final ArrayList<RsMeldingstype> liste = new ArrayList<>();
        liste.add(melding1);
        liste.add(melding2);
        validator.logAndRemoveInvalidMessages(liste);
        
        assertEquals(1, listAppender.list.size());
        assertTrue(listAppender.list.get(0).toString().contains("Valideringsfeil for melding med aarsakskode null. size must be between 0 and 2 for variabelen antallBarn=2421234."));
        
    }
}