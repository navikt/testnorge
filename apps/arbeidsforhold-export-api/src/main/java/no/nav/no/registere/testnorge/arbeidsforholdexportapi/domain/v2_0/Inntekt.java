package no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.v2_0;

import lombok.RequiredArgsConstructor;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class Inntekt implements no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.Inntekt {
    private final no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.Inntekt inntekt;
    private final String typeArbeidsforhold;
    private final String kalendermaaned;

    public LocalDate getStartdatoOpptjeningsperiode() {
        return toLocalDate(inntekt.getStartdatoOpptjeningsperiode());
    }

    public LocalDate getSluttdatoOpptjeningsperiode() {
        return toLocalDate(inntekt.getSluttdatoOpptjeningsperiode());
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

    public String getKalendermaaned() {
        return kalendermaaned;
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

    public List<Avvik> getAvvikList(){
        return inntekt.getAvvik() != null
                ? inntekt.getAvvik().stream().map(value -> new Avvik(value, "INNTEKT", typeArbeidsforhold)).collect(Collectors.toList())
                : Collections.emptyList();
    }

    public boolean hasAvvik() {
        return !getAvvikList().isEmpty();
    }
}