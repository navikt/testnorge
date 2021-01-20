package no.nav.dolly.bestilling.aareg.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.aareg.domain.Aktoer;
import no.nav.dolly.bestilling.aareg.domain.Arbeidsforhold;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdResponse;
import no.nav.dolly.domain.resultset.aareg.RsAktoerPerson;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.aareg.RsPersonAareg;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@UtilityClass
@Slf4j
public class AaregMergeUtil {

    public List<Arbeidsforhold> merge(List<Arbeidsforhold> nyeArbeidsforhold,
                                      List<ArbeidsforholdResponse> eksisterendeArbeidsforhold, String ident, boolean isLeggTil) {

        if (eksisterendeArbeidsforhold.isEmpty() || isLeggTil) {

            return appendIds(nyeArbeidsforhold, eksisterendeArbeidsforhold, ident);
        }

        List<Arbeidsforhold> arbeidsforhold = nyeArbeidsforhold.stream()
                .filter(arbforhold -> eksisterendeArbeidsforhold.stream()
                        .noneMatch(arbforhold2 ->
                                isEqual(arbforhold, arbforhold2)))
                .collect(Collectors.toList());

        return appendIds(arbeidsforhold, eksisterendeArbeidsforhold, ident);
    }

    private static boolean isEqual(Arbeidsforhold nytt, ArbeidsforholdResponse eksisterende) {

        return (isEqualOrgnummer(nytt, eksisterende) || isEqualPersonnr(nytt, eksisterende)) &&
                isEqualArbeidsAvtale(nytt, eksisterende);
    }

    private static boolean isEqualArbeidsAvtale(Arbeidsforhold nytt, ArbeidsforholdResponse eksisterende) {

        return eksisterende.getArbeidsavtaler().stream().anyMatch(arbeidsavtale ->
                arbeidsavtale.getYrke().equals(nytt.getArbeidsavtale().getYrke()) &&
                        arbeidsavtale.getArbeidstidsordning().equals(nytt.getArbeidsavtale().getArbeidstidsordning())
        );
    }

    private static boolean isEqualOrgnummer(Arbeidsforhold nytt, ArbeidsforholdResponse eksisterende) {

        return "ORG".equals(nytt.getArbeidsgiver().getAktoertype()) &&
                eksisterende.getArbeidsgiver().getType() == Aktoer.Organisasjon &&
                ((RsOrganisasjon) nytt.getArbeidsgiver()).getOrgnummer()
                        .equals(eksisterende.getArbeidsgiver().getOrganisasjonsnummer());
    }

    private static boolean isEqualPersonnr(Arbeidsforhold nytt, ArbeidsforholdResponse eksisterende) {

        return "PERS".equals(nytt.getArbeidsgiver().getAktoertype()) &&
                eksisterende.getArbeidsgiver().getType() == Aktoer.Person &&
                ((RsAktoerPerson) nytt.getArbeidsgiver()).getIdent()
                        .equals(eksisterende.getArbeidsgiver().getOffentligIdent());
    }

    private static List<Arbeidsforhold> appendIds(List<Arbeidsforhold> nyeArbeidsforhold,
                                                  List<ArbeidsforholdResponse> eksisterendeArbeidsforhold,
                                                  String ident) {

        eksisterendeArbeidsforhold.stream()
                .map(ArbeidsforholdResponse::getArbeidsforholdId)
                .forEach(arbeidsforholdId -> log.info("Appender arbeidsforholdId {} til ident {}", arbeidsforholdId, ident));

        AtomicInteger arbeidsforholdId = new AtomicInteger(
                eksisterendeArbeidsforhold.stream()
                        .map(ArbeidsforholdResponse::getArbeidsforholdId)
                        .map(id -> id.replace("-", ""))
                        .mapToInt(Integer::valueOf)
                        .max().orElse(0)
        );

        AtomicInteger permisjonId = new AtomicInteger(
                eksisterendeArbeidsforhold.stream()
                        .map(ArbeidsforholdResponse::getPermisjonPermitteringer)
                        .flatMap(permisjonPermittering -> permisjonPermittering.stream()
                                .map(ArbeidsforholdResponse.PermisjonPermittering::getPermisjonPermitteringId)
                                .map(Integer::valueOf)
                        )
                        .max(Comparator.comparing(id -> id)).orElse(0)
        );

        nyeArbeidsforhold.forEach(arbeidforhold -> {
            arbeidforhold.setArbeidsforholdID(Integer.toString(arbeidsforholdId.addAndGet(1)));
            arbeidforhold.getPermisjon().forEach(permisjon ->
                    permisjon.setPermisjonsId(Integer.toString(permisjonId.addAndGet(1))));
            arbeidforhold.setArbeidstaker(RsPersonAareg.builder()
                    .ident(ident)
                    .build());
        });

        return nyeArbeidsforhold;
    }
}
