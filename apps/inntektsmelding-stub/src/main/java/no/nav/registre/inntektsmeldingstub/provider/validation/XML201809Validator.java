package no.nav.registre.inntektsmeldingstub.provider.validation;

import no.nav.registre.inntektsmeldingstub.provider.rs.RsInntektsmelding;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class XML201809Validator {

    private XML201809Validator() {

    }

    public static void validate(RsInntektsmelding inntektsmelding) throws ValidationException {

        Map<String, Boolean> rules = new HashMap<>();

        rules.put("Må ha en arbeidsgiver.", inntektsmelding.getArbeidsgiver().isEmpty());

        List<String> errors = rules.entrySet().stream()
                .filter(s -> s.getValue().equals(true))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
