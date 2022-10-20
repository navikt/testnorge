package no.nav.dolly.bestilling.aaregrest.util;

import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.dto.aareg.v1.Organisasjon;
import no.nav.testnav.libs.dto.aareg.v1.Person;

@UtilityClass
public class AaaregUtility {

    public static boolean isEqualArbeidsforhold(Arbeidsforhold arbeidforhold1, Arbeidsforhold arbeidsforhold2) {

        return isArbeidstagerAlike(arbeidforhold1, arbeidsforhold2) &&
                (isArbeidsgiverOrganisasjonAlike(arbeidforhold1, arbeidsforhold2) ||
                        isArbeidsgiverPersonAlike(arbeidforhold1, arbeidsforhold2));
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
