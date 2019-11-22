package no.nav.registre.inntektsmeldingstub.provider.validation;

import no.nav.registre.inntektsmeldingstub.MeldingsType;
import no.nav.registre.inntektsmeldingstub.service.rs.RsInntektsmelding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class InntektsmeldingRequestValidator {

    public static void validate(List<RsInntektsmelding> meldinger, MeldingsType type, String eier) throws ValidationException {

        Map<String, Boolean> rules = new HashMap<>();
        List<String> errors = new ArrayList<>();

        rules.put("Kan ikke sende inn en tom liste med inntektsmeldinger.",
                (Objects.isNull(meldinger) || meldinger.isEmpty()));
        rules.put("Eier må være definert. Var: \'" + eier + "\'.",
                (Objects.isNull(eier) || eier.isBlank()));

        meldinger.forEach( (melding) -> {

            rules.put("Inntektsmeldingen må inneholde et arbeidsforhold",
                    Objects.isNull(melding.getArbeidsforhold()));

            try {
                if (type == MeldingsType.TYPE_2018_12) {
                    XML201812Validator.validate(melding);
                } else if (type == MeldingsType.TYPE_2018_09) {
                    XML201809Validator.validate(melding);
                }
            } catch (ValidationException e) {
                errors.addAll(e.getErrors());
            }
        });

        errors.addAll(rules.entrySet().stream()
                .filter(s -> s.getValue().equals(true))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList()));

        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}
