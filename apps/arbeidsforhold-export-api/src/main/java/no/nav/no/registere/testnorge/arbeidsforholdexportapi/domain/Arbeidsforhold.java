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
        return arbeidsforhold.getTypeArbeidsforhold();
    }

    public String getYrke() {
        return arbeidsforhold.getYrke();
    }

    public Float getAntallTimerPerUkeSomEnFullStillingTilsvarer() {
        return arbeidsforhold.getAntallTimerPerUkeSomEnFullStillingTilsvarer() != null
                ? arbeidsforhold.getAntallTimerPerUkeSomEnFullStillingTilsvarer().floatValue() : null;
    }

    public LocalDate getSisteLoennsendringsdato() {
        return toLocalDate(arbeidsforhold.getSisteLoennsendringsdato());
    }

    public LocalDate getSisteDatoForStillingsprosentendring() {
        return toLocalDate(arbeidsforhold.getSisteDatoForStillingsprosentendring());
    }

    public String getAvloenningstype() {
        return arbeidsforhold.getAvloenningstype();
    }

    public String getArbeidstidsordning() {
        return arbeidsforhold.getArbeidstidsordning();
    }

    public int getAntallPermisjoner() {
        return arbeidsforhold.getPermisjon() != null && !arbeidsforhold.getPermisjon().isEmpty()
                ? arbeidsforhold.getPermisjon().size() : 0;
    }

    public Float getStillingsprosent() {
        return arbeidsforhold.getStillingsprosent() != null
                ? arbeidsforhold.getStillingsprosent().floatValue() : null   ;
    }

    public LocalDate getStartdato() {
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
