package no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;

public class Permisjon {
    private final String ident;
    private final String kalendermaaned;
    private final String beskrivelse;
    private final Float permisjonsprosent;
    private final LocalDate startdato;
    private final LocalDate sluttdato;


    public Permisjon(no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.Permisjon permisjon, String kalendermaaned, String ident) {
        this.kalendermaaned = kalendermaaned;
        this.ident = ident;
        this.beskrivelse = permisjon.getBeskrivelse();
        this.permisjonsprosent = permisjon.getPermisjonsprosent() != null ? permisjon.getPermisjonsprosent().floatValue() : null;
        this.startdato = toLocalDate(permisjon.getStartdato());
        this.sluttdato = toLocalDate(permisjon.getSluttdato());
    }

    public String getBeskrivelse() {
        return beskrivelse;
    }

    public Float getPermisjonsprosent() {
        return permisjonsprosent;
    }

    public LocalDate getStartdato() {
        return startdato;
    }

    public LocalDate getSluttdato() {
        return sluttdato;
    }

    public String getIdent() {
        return ident;
    }

    public String getKalendermaaned() {
        return kalendermaaned;
    }

    private static LocalDate toLocalDate(XMLGregorianCalendar calendar) {
        if (calendar == null) {
            return null;
        }
        return LocalDate.of(calendar.getYear(), calendar.getMonth(), calendar.getDay());
    }
}
