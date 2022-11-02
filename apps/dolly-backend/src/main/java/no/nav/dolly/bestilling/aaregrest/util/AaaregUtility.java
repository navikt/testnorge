package no.nav.dolly.bestilling.aaregrest.util;

import lombok.experimental.UtilityClass;
import no.nav.dolly.bestilling.aaregrest.domain.ArbeidsforholdEksistens;
import no.nav.dolly.bestilling.aaregrest.domain.ArbeidsforholdRespons;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.dto.aareg.v1.Organisasjon;
import no.nav.testnav.libs.dto.aareg.v1.Person;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@UtilityClass
public class AaaregUtility {

    public static boolean isEqualArbeidsforhold(Arbeidsforhold response, Arbeidsforhold request) {

        return (isArbeidsgiverOrganisasjonAlike(response, request) ||
                        isArbeidsgiverPersonAlike(response, request)) &&
                (response.getArbeidsforholdId().equals(request.getArbeidsforholdId()) ||
                isAnsettelsesperiodeAlike(response, request));
    }

    private static boolean isAnsettelsesperiodeAlike(Arbeidsforhold response, Arbeidsforhold request) {

        return response.getAnsettelsesperiode().getPeriode().getFom().equals(request.getAnsettelsesperiode().getPeriode().getFom()) ||
                (isNotBlank(request.getAnsettelsesperiode().getSluttaarsak()) ||
                        isBlank(response.getAnsettelsesperiode().getSluttaarsak()));
    }

    public static ArbeidsforholdEksistens doEksistenssjekk(ArbeidsforholdRespons response,
                                                           List<Arbeidsforhold> request) {

        return ArbeidsforholdEksistens.builder()
                .nyeArbeidsforhold(request.stream()
                        .filter(arbeidsforhold -> response.getEksisterendeArbeidsforhold().stream()
                                .noneMatch(response1 -> isEqualArbeidsforhold(response1, arbeidsforhold)
                                ))
                        .toList())
                .eksisterendeArbeidsforhold(request.stream()
                        .filter(arbeidsforhold -> response.getEksisterendeArbeidsforhold().stream()
                                .anyMatch(response1 -> isEqualArbeidsforhold(response1, arbeidsforhold)))
                        .toList())
                .build();
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
