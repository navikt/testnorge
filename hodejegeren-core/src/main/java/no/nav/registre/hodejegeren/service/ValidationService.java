package no.nav.registre.hodejegeren.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;

@Slf4j
@Service
public class ValidationService {

    final Validator validator;
    public ValidationService() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        factory.close();
    }
    
    public void logAndRemoveInvalidMessages(List<RsMeldingstype> meldinger) {
        List<RsMeldingstype> removeTheseMessages = new ArrayList<>();
        
        for (RsMeldingstype melding : meldinger) {
            final Set<ConstraintViolation<RsMeldingstype>> violations = validator.validate(melding);
            if (!violations.isEmpty()) {
                removeTheseMessages.add(melding);
                logValidation(melding, violations);
            }
        }
        meldinger.removeAll(removeTheseMessages);
    }
    
    private void logValidation(RsMeldingstype melding, Set<ConstraintViolation<RsMeldingstype>> violations) {
        StringBuilder messageBuilder = new StringBuilder()
                .append("Valideringsfeil for melding med aarsakskode ")
                .append(melding.getAarsakskode()).append(". ");
        for (ConstraintViolation violation : violations) {
            messageBuilder
                    .append(violation.getMessage())
                    .append(" for variabelen ")
                    .append(violation.getPropertyPath().toString())
                    .append('=').append(violation.getInvalidValue()).append(". ");
        }
        log.error(messageBuilder.toString());
    }
}
