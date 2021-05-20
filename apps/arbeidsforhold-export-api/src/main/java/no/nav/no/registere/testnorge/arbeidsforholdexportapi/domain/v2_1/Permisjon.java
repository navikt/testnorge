package no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.v2_1;

import javax.xml.datatype.XMLGregorianCalendar;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Permisjon implements no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain.Permisjon {
    private final String ident;
    private final String kalendermaaned;
    private final String kildereferanse;
    private final String beskrivelse;
    private final Float permisjonsprosent;
    private final LocalDate startdato;
    private final LocalDate sluttdato;
    private final String typeArbeidsforhold;
    private final List<Avvik> avvikList;

    public Permisjon(
            no.nav.registre.testnorge.xsd.arbeidsforhold.v2_1.Permisjon permisjon,
            String kalendermaaned,
            String ident,
            String kildereferans,
            String typeArbeidsforhold
    ) {
        this.kalendermaaned = kalendermaaned;
        this.ident = ident;
        this.beskrivelse = permisjon.getBeskrivelse();
        this.permisjonsprosent = permisjon.getPermisjonsprosent() != null ? permisjon.getPermisjonsprosent().floatValue() : null;
        this.startdato = toLocalDate(permisjon.getStartdato());
        this.sluttdato = toLocalDate(permisjon.getSluttdato());
        this.kildereferanse = kildereferans;
        this.avvikList = permisjon.getAvvik() != null
                ? permisjon.getAvvik().stream().map(value -> new Avvik(value, "PERMISJON", typeArbeidsforhold)).collect(Collectors.toList())
                : Collections.emptyList();
        this.typeArbeidsforhold = typeArbeidsforhold;
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

    public String getKildereferanse() {
        return kildereferanse;
    }

    public String getIdent() {
        return ident;
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

    public boolean hasAvvik() {
        return !avvikList.isEmpty();
    }

    public List<Avvik> getAvvikList() {
        return avvikList;
    }
}
