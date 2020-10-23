package no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;

public class Permisjon {
    private final String ident;
    private final String kalendermaaned;
    private final no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.Permisjon permisjon;

    public Permisjon(no.nav.registre.testnorge.xsd.arbeidsforhold.v2_0.Permisjon permisjon, String kalendermaaned, String ident) {
        this.permisjon = permisjon;
        this.kalendermaaned = kalendermaaned;
        this.ident = ident;
    }

    public String getBeskrivelse() {
        return permisjon.getBeskrivelse();
    }

    public String getIdent() {
        return ident;
    }

    public String getKalendermaaned() {
        return kalendermaaned;
    }

    public Float getPermisjonsprosent() {
        return permisjon.getPermisjonsprosent() != null ? permisjon.getPermisjonsprosent().floatValue() : null;
    }

    public LocalDate getStartdato() {
        return toLocalDate(permisjon.getStartdato());
    }

    public LocalDate getSluttdato() {
        return toLocalDate(permisjon.getSluttdato());
    }

    private static LocalDate toLocalDate(XMLGregorianCalendar calendar) {
        if (calendar == null) {
            return null;
        }
        return LocalDate.of(calendar.getYear(), calendar.getMonth(), calendar.getDay());
    }
}
