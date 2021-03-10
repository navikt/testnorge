package no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain;

import lombok.RequiredArgsConstructor;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;

@RequiredArgsConstructor
public class Inntekt {
    private final no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.Inntekt inntekt;
    private final String typeArbeidsforhold;

    public LocalDate getStartdatoOpptjeningsperiode() {
        return toLocalDate(inntekt.getStartdatoOpptjeningsperiode());
    }

    public LocalDate getSluttdatoOpptjeningsperiode() {
        return toLocalDate(inntekt.getSluttdatoOpptjeningsperiode());
    }

    public String getArbeidsforholdId() {
        return inntekt.getArbeidsforholdId();
    }

    public Float getAntall() {
        return inntekt.getLoennsinntekt() != null && inntekt.getLoennsinntekt().getAntall() != null
                ? inntekt.getLoennsinntekt().getAntall().floatValue()
                : null;
    }

    public String getOpptjeningsland() {
        return inntekt.getLoennsinntekt() != null && inntekt.getLoennsinntekt().getSpesifikasjon() != null
                ? inntekt.getLoennsinntekt().getSpesifikasjon().getOpptjeningsland()
                : null;
    }

    public String getTypeArbeidsforhold() {
        return typeArbeidsforhold;
    }

    private static LocalDate toLocalDate(XMLGregorianCalendar calendar) {
        if (calendar == null) {
            return null;
        }
        return LocalDate.of(calendar.getYear(), calendar.getMonth(), calendar.getDay());
    }
}