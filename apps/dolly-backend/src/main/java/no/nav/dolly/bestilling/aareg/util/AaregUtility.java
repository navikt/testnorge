package no.nav.dolly.bestilling.aareg.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.dolly.domain.resultset.aareg.RsAareg.Identifikasjon;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.dto.aareg.v1.Organisasjon;
import no.nav.testnav.libs.dto.aareg.v1.PermisjonPermittering;
import no.nav.testnav.libs.dto.aareg.v1.Person;
import org.apache.commons.lang3.StringUtils;

import java.time.YearMonth;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@UtilityClass
@Slf4j
public class AaregUtility {

    public static boolean isEqualArbeidsforhold(Arbeidsforhold response, Arbeidsforhold request, Map<String, Identifikasjon> identifikasjon) {

        return identifikasjon.values().stream().anyMatch(id ->
                response.getNavArbeidsforholdId().equals(id.getNavArbeidsforholdId())) ||
                (isArbeidsgiverOrganisasjonAlike(response, request) ||
                        isArbeidsgiverPersonAlike(response, request)) &&
                        response.getArbeidsforholdId().equals(request.getArbeidsforholdId());
    }

    public static int getMaxArbeidsforholdId(Collection<Arbeidsforhold> arbeidsforholdList) {

        return arbeidsforholdList.stream()
                .map(Arbeidsforhold::getArbeidsforholdId)
                .filter(StringUtils::isNotBlank)
                .map(Integer::parseInt)
                .max(Integer::compareTo)
                .orElse(0);
    }

    public static int getMaxPermisjonPermitteringId(Collection<Arbeidsforhold> arbeidsforholdList) {

        return arbeidsforholdList.stream()
                .map(Arbeidsforhold::getPermisjonPermitteringer)
                .flatMap(Collection::stream)
                .map(PermisjonPermittering::getPermisjonPermitteringId)
                .filter(StringUtils::isNotBlank)
                .map(Integer::parseInt)
                .max(Integer::compareTo)
                .orElse(0);
    }

    public static Arbeidsforhold appendArbeidsforholdId(Arbeidsforhold arbeidsforhold, boolean nyttArbeidsforhold,
                                                        List<Arbeidsforhold> eksisterendeListe, Map<String, Identifikasjon> identifikasjon,
                                                        AtomicInteger arbeidsforholdId) {

        if (nyttArbeidsforhold) {

            arbeidsforhold.setArbeidsforholdId(Integer.toString(arbeidsforholdId.incrementAndGet()));
            arbeidsforhold.setNavArbeidsforholdPeriode(nonNull(arbeidsforhold.getNavArbeidsforholdPeriode()) ?
                    arbeidsforhold.getNavArbeidsforholdPeriode() : YearMonth.now());

        } else {

            var eksisterende = eksisterendeListe.stream()
                    .filter(eksisterende1 -> isEqualArbeidsforhold(eksisterende1, arbeidsforhold, identifikasjon))
                    .findFirst().orElse(new Arbeidsforhold());
            arbeidsforhold.setNavArbeidsforholdId(eksisterende.getNavArbeidsforholdId());
            arbeidsforhold.setArbeidsforholdId(eksisterende.getArbeidsforholdId());
            arbeidsforhold.setNavArbeidsforholdPeriode(nonNull(eksisterende.getNavArbeidsforholdPeriode()) ?
                    eksisterende.getNavArbeidsforholdPeriode() : YearMonth.now());
        }

        appendPermisjonPermitteringId(arbeidsforhold);

        return arbeidsforhold;
    }

    public static boolean isNyttArbeidsforhold(List<Arbeidsforhold> response, Arbeidsforhold request) {

        return isBlank(request.getArbeidsforholdId()) &&
                response.stream().noneMatch(eksisterende ->
                                (isArbeidsgiverOrganisasjonAlike(eksisterende, request) ||
                                        isArbeidsgiverPersonAlike(eksisterende, request)) &&
                                        eksisterende.getType().equals(request.getType()));
    }

    private static void appendPermisjonPermitteringId(Arbeidsforhold arbeidsforhold) {

        val id = new AtomicInteger(0);

        arbeidsforhold.getPermisjonPermitteringer().stream()
                .filter(permisjon -> isBlank(permisjon.getPermisjonPermitteringId()))
                .forEach(permisjon ->
                        permisjon.setPermisjonPermitteringId(Integer.toString(id.incrementAndGet())));
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