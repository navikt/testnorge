package no.nav.dolly.bestilling.aareg.util;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;
import no.nav.dolly.bestilling.aareg.domain.Arbeidsforhold;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdResponse;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;

@UtilityClass
public class AaregMergeUtil {

    public List<Arbeidsforhold> merge(List<Arbeidsforhold> nyeArbeidsforhold, ArbeidsforholdResponse eksisterendeArbeidsforhold, boolean isLeggTil) {

        if (eksisterendeArbeidsforhold.getArbeidsforhold().isEmpty() || isLeggTil) {

            return appendArbeidforholdId(nyeArbeidsforhold, eksisterendeArbeidsforhold);
        }

        List<Arbeidsforhold> arbeidsforhold = nyeArbeidsforhold.stream()
                .filter(arbforhold -> eksisterendeArbeidsforhold.getArbeidsforhold().stream()
                        .noneMatch(arbforhold2 -> arbforhold2.equals(arbforhold)))
                .collect(Collectors.toList());

        (isMatchArbgivOrgnummer(arbfInput.getArbeidsgiver(), getIdentifyingNumber(arbfFraAareg)) ||
                isMatchArbgivPersonnummer(arbfInput.getArbeidsgiver(), getIdentifyingNumber(arbfFraAareg))) &&
                arbfInput.getArbeidsforholdID().equals(getArbforholdId(arbfFraAareg)))


    }

    private static boolean isEqual(Arbeidsforhold nytt, ArbeidsforholdResponse.Arbeidsforhold eksisterende) {

        return isOrgnummer(nytt, eksisterende) &&
                ((RsOrganisasjon) nytt.getArbeidsgiver()).getOrgnummer().equals(eksisterende.getArbeidsgiver().getOrganisasjonsnummer());
    }

    private static boolean isOrgnummer(Arbeidsforhold nytt, ArbeidsforholdResponse.Arbeidsforhold eksisterende) {
        return "ORG".equals(nytt.getArbeidsgiver().getAktoertype()) &&
                eksisterende.getArbeidsgiver().getType() == ArbeidsforholdResponse.Aktoer.Organisasjon;
    }

    private static List<Arbeidsforhold> appendArbeidforholdId(List<Arbeidsforhold> nyeArbeidsforhold,
            ArbeidsforholdResponse eksisterendeArbeidsforhold) {

        AtomicInteger arbeidsforholdId = new AtomicInteger(
                eksisterendeArbeidsforhold.getArbeidsforhold().stream()
                        .map(ArbeidsforholdResponse.Arbeidsforhold::getArbeidsforholdId).map(Integer::new)
                        .max(Comparator.reverseOrder()).orElse(0)
        );

        nyeArbeidsforhold.forEach(arbeidforhold ->
                arbeidforhold.setArbeidsforholdID(Integer.toString(arbeidsforholdId.addAndGet(1))));

        return nyeArbeidsforhold;
    }
}
