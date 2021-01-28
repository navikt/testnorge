package no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.util.List;

import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.Inntektsmottaker;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.Leveranse;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.Virksomhet;


public class Arbeidsforhold {
    private final Leveranse leveranse;
    private final Virksomhet virksomhet;
    private final Inntektsmottaker inntektsmottaker;
    private final no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.Arbeidsforhold arbeidsforhold;
    private final Permisjoner permisjoner;

    public Arbeidsforhold(
            Leveranse leveranse,
            Virksomhet virksomhet,
            Inntektsmottaker inntektsmottaker,
            no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.Arbeidsforhold arbeidsforhold
    ) {
        this.leveranse = leveranse;
        this.virksomhet = virksomhet;
        this.inntektsmottaker = inntektsmottaker;
        this.arbeidsforhold = arbeidsforhold;
        this.permisjoner = Permisjoner.from(arbeidsforhold.getPermisjon(), getKalendermaaned(), getIdent());
    }

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

    public Float getStillingsprosent() {
        return arbeidsforhold.getStillingsprosent() != null
                ? arbeidsforhold.getStillingsprosent().floatValue() : null;
    }

    public LocalDate getStartdato() {
        return toLocalDate(arbeidsforhold.getStartdato());
    }

    public LocalDate getSluttdato() {
        return toLocalDate(arbeidsforhold.getSluttdato());
    }


    public int getAntallVelferdspermisjon() {
        return permisjoner.getAntallVelferdspermisjon();
    }

    public int getAntallPermisjonMedForeldrepenger() {
        return permisjoner.getAntallPermisjonMedForeldrepenger();
    }

    public int getAntallPermittering() {
        return permisjoner.getAntallPermittering();
    }

    public int getAntallPermisjon() {
        return permisjoner.getAntallPermisjon();
    }

    public int getAntallPermisjonVedMilitaertjeneste() {
        return permisjoner.getAntallPermisjonVedMilitaertjeneste();
    }

    public int getAntallUtdanningspermisjon() {
        return permisjoner.getAntallUtdanningspermisjon();
    }

    public List<Permisjon> getPermisjoner(){
        return permisjoner.getList();
    }


    private static LocalDate toLocalDate(XMLGregorianCalendar calendar) {
        if (calendar == null) {
            return null;
        }
        return LocalDate.of(calendar.getYear(), calendar.getMonth(), calendar.getDay());
    }

    public String getSkipsregister(){
        return arbeidsforhold.getFartoey() != null ? arbeidsforhold.getFartoey().getSkipsregister() : null;
    }

    public String getSkipstype(){
        return arbeidsforhold.getFartoey() != null ? arbeidsforhold.getFartoey().getSkipstype() : null;
    }

    public String getFartsomraade(){
        return arbeidsforhold.getFartoey() != null ? arbeidsforhold.getFartoey().getFartsomraade() : null;
    }
}
