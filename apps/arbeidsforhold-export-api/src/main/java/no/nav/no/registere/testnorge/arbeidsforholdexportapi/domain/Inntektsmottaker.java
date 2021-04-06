package no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.Leveranse;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.Virksomhet;


public class Inntektsmottaker {
    private final List<Arbeidsforhold> arbeidsforholdList;
    private final List<Inntekt> inntektList;

    public Inntektsmottaker(
            Leveranse leveranse,
            Virksomhet virksomhet,
            no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.Inntektsmottaker inntektsmottaker
    ) {
        var counter = new AtomicInteger();
        this.arbeidsforholdList = inntektsmottaker.getArbeidsforhold()
                .stream()
                .map(value -> {
                    var index = counter.getAndIncrement();
                    int antallInntekter;
                    if (index == 0) {
                        antallInntekter = getAntallInntekter(value.getArbeidsforholdId(), inntektsmottaker.getInntekt())
                                + getAntallUtenArbeidsforholdMatch(inntektsmottaker.getArbeidsforhold(), inntektsmottaker.getInntekt());
                    } else {
                        antallInntekter = getAntallInntekter(value.getArbeidsforholdId(), inntektsmottaker.getInntekt());
                    }
                    return new Arbeidsforhold(leveranse, virksomhet, inntektsmottaker, antallInntekter, value);
                }).collect(Collectors.toList());

        this.inntektList = inntektsmottaker.getInntekt()
                .stream()
                .map(value -> new Inntekt(
                        value,
                        getTypeArbeidsforholdFraIntekt(value, inntektsmottaker.getArbeidsforhold()),
                        leveranse.getKalendermaaned().toString()
                ))
                .collect(Collectors.toList());
    }

    private static int getAntallUtenArbeidsforholdMatch(
            List<no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.Arbeidsforhold> arbeidsforholdList,
            List<no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.Inntekt> inntektList
    ) {
        if (inntektList == null) {
            return 0;
        }
        var ids = inntektList.stream().map(no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.Inntekt::getArbeidsforholdId).collect(Collectors.toList());
        return (int) ids.stream().filter(id -> !contains(id, arbeidsforholdList)).count();
    }

    private static boolean contains(String arbeidsforholdId, List<no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.Arbeidsforhold> arbeidsforholdList) {
        if (arbeidsforholdId == null) {
            return false;
        }
        return arbeidsforholdList
                .stream()
                .anyMatch(value -> value.getArbeidsforholdId() != null && value.getArbeidsforholdId().equals(arbeidsforholdId));
    }

    private static int getAntallInntekter(String arbeidsforholdId, List<no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.Inntekt> inntektList) {
        if (inntektList == null) {
            return 0;
        }

        return (int) inntektList
                .stream()
                .filter(value -> value.getArbeidsforholdId() != null && value.getArbeidsforholdId().equals(arbeidsforholdId))
                .count();
    }

    private static String getTypeArbeidsforholdFraIntekt(
            no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.Inntekt inntekt,
            List<no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.Arbeidsforhold> arbeidsforholdList
    ) {
        if (arbeidsforholdList.isEmpty()) {
            return null;
        }
        if (inntekt.getArbeidsforholdId() == null) {
            return arbeidsforholdList.get(0).getTypeArbeidsforhold();
        }
        return arbeidsforholdList
                .stream()
                .filter(value -> value.getArbeidsforholdId().equals(inntekt.getArbeidsforholdId()))
                .findFirst()
                .orElse(arbeidsforholdList.get(0))
                .getTypeArbeidsforhold();
    }

    public List<Arbeidsforhold> getArbeidsforholdList() {
        return arbeidsforholdList;
    }

    public List<Inntekt> getInntektList() {
        return inntektList;
    }
}
