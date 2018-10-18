package no.nav.registre.hodejegeren.service;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.Test;

import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype1Felter;

public class ValidationServiceTest {
    
    ValidationService validator = new ValidationService();
    
    @Test
    public void shouldValidate() {
        final RsMeldingstype1Felter melding = RsMeldingstype1Felter.builder().antallBarn("2421234").build();
        final List<Set<ConstraintViolation<RsMeldingstype>>> constraintViolations = validator.validerMeldinger(Arrays.asList(melding, melding));
        for (ConstraintViolation violation : constraintViolations.get(0)) {
            assertEquals("size must be between 0 and 2", violation.getMessage());
            assertEquals("2421234", violation.getInvalidValue());
            assertEquals("antallBarn", violation.getPropertyPath().toString());
        }
    }
}