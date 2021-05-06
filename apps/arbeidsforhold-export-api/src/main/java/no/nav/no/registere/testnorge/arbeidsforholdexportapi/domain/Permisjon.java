package no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain;

import java.time.LocalDate;
import java.util.List;

public interface Permisjon {
    String getBeskrivelse();

    Float getPermisjonsprosent();

    LocalDate getStartdato();

    LocalDate getSluttdato();

    String getKildereferanse();

    String getIdent();

    String getKalendermaaned();

    String getTypeArbeidsforhold();

    boolean hasAvvik();

    List<? extends Avvik> getAvvikList();
}
