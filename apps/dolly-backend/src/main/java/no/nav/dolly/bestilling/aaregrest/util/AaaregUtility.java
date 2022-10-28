package no.nav.dolly.bestilling.aaregrest.util;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.dto.aareg.v1.Organisasjon;
import no.nav.testnav.libs.dto.aareg.v1.Person;

import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@UtilityClass
public class AaaregUtility {

    public static boolean isEqualArbeidsforhold(Arbeidsforhold arbeidforhold1, Arbeidsforhold arbeidsforhold2) {

        return isArbeidstagerAlike(arbeidforhold1, arbeidsforhold2) &&
                (isArbeidsgiverOrganisasjonAlike(arbeidforhold1, arbeidsforhold2) ||
                        isArbeidsgiverPersonAlike(arbeidforhold1, arbeidsforhold2));
    }

    public static void appendPermisjonPermitteringId(Arbeidsforhold arbeidsforhold, Arbeidsforhold eksisterende) {

        var permisjonPermitteringId = new AtomicInteger(isNull(eksisterende) ? 0 :
                eksisterende.getPermisjonPermitteringer().size());

        if (isNull(eksisterende)) {
            arbeidsforhold.getPermisjonPermitteringer().stream()
                    .filter(permisjon -> isBlank(permisjon.getPermisjonPermitteringId()))
                    .forEach(permisjon ->
                            permisjon.setPermisjonPermitteringId(Integer.toString(permisjonPermitteringId.incrementAndGet())));
        } else {
            arbeidsforhold.getPermisjonPermitteringer().stream()
                    .filter(permisjon -> isBlank(permisjon.getPermisjonPermitteringId()))
                    .forEach(permisjon -> permisjon.setPermisjonPermitteringId(eksisterende.getPermisjonPermitteringer().stream()
                            .anyMatch(eksist -> eksist.equals(permisjon)) ?
                            eksisterende.getPermisjonPermitteringer().stream()
                                    .filter(eksist -> eksist.equals(permisjon))
                                    .findFirst().get().getPermisjonPermitteringId() :
                            Integer.toString(permisjonPermitteringId.incrementAndGet())));
        }
    }

    private static boolean isArbeidstagerAlike(Arbeidsforhold arbeidsforhold1, Arbeidsforhold arbeidsforhold2) {

        return arbeidsforhold1.getArbeidstaker().getOffentligIdent().equals(arbeidsforhold2.getArbeidstaker().getOffentligIdent());
    }

    private static boolean isArbeidsgiverPersonAlike(Arbeidsforhold arbeidsforhold1, Arbeidsforhold arbeidsforhold2) {

        return arbeidsforhold1.getArbeidsgiver() instanceof Person person1 &&
                arbeidsforhold2.getArbeidsgiver() instanceof Person person2 &&
                person1.getOffentligIdent().equals(person2.getOffentligIdent());
    }

    private static boolean isArbeidsgiverOrganisasjonAlike(Arbeidsforhold arbeidsforhold1, Arbeidsforhold arbeidsforhold2) {

        return arbeidsforhold1.getArbeidsgiver() instanceof Organisasjon organisasjon1 &&
                arbeidsforhold2.getArbeidsgiver() instanceof Organisasjon organisasjon2 &&
                organisasjon1.getOrganisasjonsnummer().equals(organisasjon2.getOrganisasjonsnummer());
    }
}
