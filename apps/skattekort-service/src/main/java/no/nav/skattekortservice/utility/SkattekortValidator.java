package no.nav.skattekortservice.utility;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.dto.skattekortservice.v1.ArbeidsgiverSkatt;
import no.nav.testnav.libs.dto.skattekortservice.v1.Skattekort;
import no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortRequestDTO;
import no.nav.testnav.libs.dto.skattekortservice.v1.Skattekortmelding;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@UtilityClass
public class SkattekortValidator {

    public static void validate(SkattekortRequestDTO skattekort) {

        validateArbeidsgiver(skattekort);

        validateArbeidstager(skattekort);

        validateSkattekort(skattekort);
    }

    private static void validateSkattekort(SkattekortRequestDTO skattekort) {

        skattekort.getArbeidsgiver().stream()
                .map(ArbeidsgiverSkatt::getArbeidstaker)
                .flatMap(Collection::stream)
                .map(Skattekortmelding::getSkattekort)
                .map(Skattekort::getForskuddstrekk)
                .flatMap(Collection::stream)
                .forEach(trekktype -> {
                    if (trekktype.isAllEmpty()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "En av Forskuddstrekk, Frikort, Trekkprosent og Trekktabell må angis per trekktype");
                    } else if (trekktype.isAmbiguous()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Kun én av Forskuddstrekk, Frikort, Trekkprosent og Trekktabell kan angis per trekktype");
                    }
                });
    }

    private static void validateArbeidstager(SkattekortRequestDTO skattekort) {

        skattekort.getArbeidsgiver().stream()
                .map(ArbeidsgiverSkatt::getArbeidstaker)
                .flatMap(Collection::stream)
                .forEach(arbeidstaker -> {
                    if (arbeidstaker.isEmptyArbeidstakeridentifikator()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Arbeidstakeridentifikator må være satt");
                    } else if (arbeidstaker.isEmptyInntektsaar()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "Inntektsår må være satt");
                    }
                });
    }

    private static void validateArbeidsgiver(SkattekortRequestDTO skattekort) {

        skattekort.getArbeidsgiver().stream()
                .map(ArbeidsgiverSkatt::getArbeidsgiveridentifikator)
                .forEach(arbeidsgiver -> {
                    if (arbeidsgiver.isAllEmpty()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "arbeidsgiveridentifikator må inneholde enten " +
                                        "organisasjonsnummer eller personidentifikator");

                    } else if (arbeidsgiver.isAmbiguous()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                                "arbeidsgiveridentifikator er uklar, " +
                                        "både organisasjonsnummer og personidentifikator er angitt");
                    }
                });
    }
}
