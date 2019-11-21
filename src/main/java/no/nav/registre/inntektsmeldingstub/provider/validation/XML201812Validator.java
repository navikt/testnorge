package no.nav.registre.inntektsmeldingstub.provider.validation;

import no.nav.registre.inntektsmeldingstub.service.rs.Inntektsmelding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class XML201812Validator {

    public static void validate(Inntektsmelding inntektsmelding) throws ValidationException {

        Map<String, Boolean> rules = new HashMap<>();

        rules.put("En inntektsmelding kan kun ha én og bare én arbeidsgiver.",
                Objects.isNull(inntektsmelding.getArbeidsgiver()) == Objects.isNull(inntektsmelding.getArbeidsgiverPrivat()));

        List<String> errors = rules.entrySet().stream()
                .filter(s -> s.getValue().equals(true))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
