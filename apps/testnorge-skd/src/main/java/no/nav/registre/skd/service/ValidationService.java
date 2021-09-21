package no.nav.registre.skd.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import no.nav.registre.skd.domain.Endringskoder;
import no.nav.registre.skd.exceptions.TomResponsFraTpsSyntException;
import no.nav.registre.skd.skdmelding.RsMeldingstype;

@Slf4j
@Service
public class ValidationService {

    private final Validator validator;

    public ValidationService() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        factory.close();
    }

    public void logAndRemoveInvalidMessages(
            List<RsMeldingstype> meldinger,
            Endringskoder endringskode
    ) {
        List<RsMeldingstype> messagesToRemove = new ArrayList<>();
        meldinger.removeAll(Collections.singleton(null));
        if (meldinger.isEmpty()) {
            throw new TomResponsFraTpsSyntException("Fikk tom respons fra TPS-Synt for endringskode " + endringskode.getEndringskode());
        }

        for (var melding : meldinger) {
            final var violations = validator.validate(melding);
            if (!violations.isEmpty()) {
                messagesToRemove.add(melding);
                logValidation(melding, violations);
            }
        }
        meldinger.removeAll(messagesToRemove);
    }

    private void logValidation(
            RsMeldingstype melding,
            Set<ConstraintViolation<RsMeldingstype>> violations
    ) {
        var messageBuilder = new StringBuilder()
                .append("Valideringsfeil for melding med aarsakskode ")
                .append(melding.getAarsakskode()).append(". ");
        for (var violation : violations) {
            messageBuilder
                    .append(violation.getMessage())
                    .append(" for variabelen ")
                    .append(violation.getPropertyPath().toString())
                    .append('=').append(violation.getInvalidValue()).append(". ");
        }
        log.error(messageBuilder.toString());
    }
}
