package no.nav.registre.hodejegeren.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.springframework.stereotype.Service;

import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;

@Service
public class ValidationService {
    
    public List<Set<ConstraintViolation<RsMeldingstype>>> validerMeldinger(List<RsMeldingstype> meldinger) {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        List report = new ArrayList();
        for (RsMeldingstype melding : meldinger
                ) {
            report.add(validator.validate(melding));
        }
        return report;
    }
}
