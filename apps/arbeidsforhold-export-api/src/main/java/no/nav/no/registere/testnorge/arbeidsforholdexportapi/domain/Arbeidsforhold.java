package no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain;

import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;
import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;

import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.Inntektsmottaker;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.Leveranse;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.Virksomhet;

@RequiredArgsConstructor
public class Arbeidsforhold {
    private final Leveranse leveranse;
    private final Virksomhet virksomhet;
    private final Inntektsmottaker inntektsmottaker;
    private final no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.Arbeidsforhold arbeidsforhold;

    public String getKalendermaaned() {
        return leveranse.getKalendermaaned().toString();
    }

    public String getOpplysningspliktigOrgnummer() {
        return leveranse.getOpplysningspliktig().getNorskIdentifikator();
    }

    public String getVirksomhetOrgnummer() {
        return virksomhet.getNorskIdentifikator();
    }

    public String getIdent() {
        return inntektsmottaker.getNorskIdentifikator();
    }

    public String getArbeidsforholdId() {
        return arbeidsforhold.getArbeidsforholdId();
    }

    public String getArbeidsforholdType() {
        return arbeidsforhold.getAvloenningstype();
    }

    public String getYrkekode() {
        return arbeidsforhold.getYrke();
    }

    public Float getStillingsprosent() {
        return arbeidsforhold.getStillingsprosent().floatValue();
    }

    public LocalDate getStatdato() {
        return toLocalDate(arbeidsforhold.getStartdato());
    }

    public LocalDate getSluttdato() {
        return toLocalDate(arbeidsforhold.getSluttdato());
    }

    private static LocalDate toLocalDate(@Nullable XMLGregorianCalendar calendar) {
        if (calendar == null) {
            return null;
        }
        return LocalDate.of(calendar.getYear(), calendar.getMonth(), calendar.getDay());
    }
}
