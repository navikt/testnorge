package no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.util.List;

import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.Inntektsmottaker;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.Leveranse;
import no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.Virksomhet;


public class Arbeidsforhold {
    private final String kalendermaaned;
    private final String opplysningspliktigOrgnummer;
    private final String virksomhetOrgnummer;
    private final String ident;
    private final String arbeidsforholdId;
    private final String arbeidsforholdType;
    private final String yrke;
    private final Float antallTimerPerUkeSomEnFullStillingTilsvarer;
    private final LocalDate sisteLoennsendringsdato;
    private final LocalDate sisteDatoForStillingsprosentendring;
    private final String avloenningstype;
    private final String arbeidstidsordning;
    private final Float stillingsprosent;
    private final LocalDate startdato;
    private final LocalDate sluttdato;
    private final String skipsregister;
    private final String skipstype;
    private final String fartsomraade;
    private final Permisjoner permisjoner;

    public Arbeidsforhold(
            Leveranse leveranse,
            Virksomhet virksomhet,
            Inntektsmottaker inntektsmottaker,
            no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.Arbeidsforhold arbeidsforhold
    ) {
        this.virksomhetOrgnummer = virksomhet.getNorskIdentifikator();
        this.kalendermaaned = leveranse.getKalendermaaned().toString();
        this.opplysningspliktigOrgnummer = leveranse.getOpplysningspliktig().getNorskIdentifikator();
        this.ident = inntektsmottaker.getNorskIdentifikator();
        this.permisjoner = Permisjoner.from(arbeidsforhold.getPermisjon(), getKalendermaaned(), getIdent());
        this.arbeidsforholdId = arbeidsforhold.getArbeidsforholdId();
        this.arbeidsforholdType = arbeidsforhold.getTypeArbeidsforhold();
        this.yrke = arbeidsforhold.getYrke();
        this.antallTimerPerUkeSomEnFullStillingTilsvarer = arbeidsforhold.getAntallTimerPerUkeSomEnFullStillingTilsvarer() != null
                ? arbeidsforhold.getAntallTimerPerUkeSomEnFullStillingTilsvarer().floatValue() : null;
        this.sisteLoennsendringsdato = toLocalDate(arbeidsforhold.getSisteLoennsendringsdato());
        this.sisteDatoForStillingsprosentendring = toLocalDate(arbeidsforhold.getSisteDatoForStillingsprosentendring());
        this.avloenningstype = arbeidsforhold.getAvloenningstype();
        this.arbeidstidsordning = arbeidsforhold.getArbeidstidsordning();
        this.stillingsprosent = arbeidsforhold.getStillingsprosent() != null
                ? arbeidsforhold.getStillingsprosent().floatValue() : null;
        this.startdato = toLocalDate(arbeidsforhold.getStartdato());
        this.sluttdato = toLocalDate(arbeidsforhold.getSluttdato());
        this.skipsregister = arbeidsforhold.getFartoey() != null ? arbeidsforhold.getFartoey().getSkipsregister() : null;
        this.skipstype = arbeidsforhold.getFartoey() != null ? arbeidsforhold.getFartoey().getSkipstype() : null;
        this.fartsomraade = arbeidsforhold.getFartoey() != null ? arbeidsforhold.getFartoey().getFartsomraade() : null;
    }

    public String getArbeidsforholdId() {
        return arbeidsforholdId;
    }

    public String getArbeidsforholdType() {
        return arbeidsforholdType;
    }

    public String getYrke() {
        return yrke;
    }

    public Float getAntallTimerPerUkeSomEnFullStillingTilsvarer() {
        return antallTimerPerUkeSomEnFullStillingTilsvarer;
    }

    public LocalDate getSisteLoennsendringsdato() {
        return sisteLoennsendringsdato;
    }

    public LocalDate getSisteDatoForStillingsprosentendring() {
        return sisteDatoForStillingsprosentendring;
    }

    public String getAvloenningstype() {
        return avloenningstype;
    }

    public String getArbeidstidsordning() {
        return arbeidstidsordning;
    }

    public Float getStillingsprosent() {
        return stillingsprosent;
    }

    public LocalDate getStartdato() {
        return startdato;
    }

    public LocalDate getSluttdato() {
        return sluttdato;
    }

    public String getSkipsregister() {
        return skipsregister;
    }

    public String getSkipstype() {
        return skipstype;
    }

    public String getFartsomraade() {
        return fartsomraade;
    }

    public String getKalendermaaned() {
        return kalendermaaned;
    }

    public String getOpplysningspliktigOrgnummer() {
        return opplysningspliktigOrgnummer;
    }

    public String getVirksomhetOrgnummer() {
        return virksomhetOrgnummer;
    }

    public String getIdent() {
        return ident;
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

    public List<Permisjon> getPermisjoner() {
        return permisjoner.getList();
    }


    private static LocalDate toLocalDate(XMLGregorianCalendar calendar) {
        if (calendar == null) {
            return null;
        }
        return LocalDate.of(calendar.getYear(), calendar.getMonth(), calendar.getDay());
    }
}
