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

        meldinger.forEach((melding) -> {
            rules.put("Inntektsmeldingen må inneholde en ytelse",
                    Objects.isNull(melding.getYtelse()) || melding.getYtelse().isBlank());
            rules.put("Inntektsmeldingen må inneholde en årsak til innsending",
                    Objects.isNull(melding.getAarsakTilInnsending()) || melding.getAarsakTilInnsending().isBlank());
            rules.put("Inntektsmeldingen må inneholde arbeidstakers fødselsnummer",
                    Objects.isNull(melding.getArbeidstakerFnr()) || melding.getArbeidstakerFnr().length() != 11);
            rules.put("Inntektsmeldingen må inneholde et arbeidsforhold",
                    Objects.isNull(melding.getArbeidsforhold()));
            rules.put("Inntektsmeldingen må ha et avsendersystem.",
                    (Objects.isNull(melding.getAvsendersystem())));
//            rules.put("Arbeidsgivers informasjon må være korrekt utfylt.",
//                    (melding.getArbeidsgiver() != null) &&
//                            (Objects.isNull(melding.getArbeidsgiver().getVirksomhetsnummer()) ||
//                                    Objects.isNull(melding.getArbeidsgiver().getKontaktinformasjon())));
//            rules.put("Privat arbeidsgivers informasjon må være korrekt utfylt.",
//                    (melding.getArbeidsgiverPrivat() != null) &&
//                            (Objects.isNull(melding.getArbeidsgiverPrivat().getArbeidsgiverFnr()) ||
//                                    Objects.isNull(melding.getArbeidsgiverPrivat().getKontaktinformasjon())));


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
