package no.nav.testnav.inntektsmeldinggeneratorservice.provider.validation;

import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsInntektsmelding;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

@Component
public class XML201809Validator {

    private XML201809Validator() {

    }

    public static void validate(RsInntektsmelding inntektsmelding) throws ValidationException {

        Map<String, Boolean> rules = new HashMap<>();

        rules.put("Må ha en arbeidsgiver.", isNull(inntektsmelding.getArbeidsgiver()));

        List<String> errors = rules.entrySet().stream()
                .filter(s -> s.getValue().equals(true))
                .map(Map.Entry::getKey)
                .toList();

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
