package no.nav.no.registere.testnorge.arbeidsforholdexportapi.domain;

import java.time.LocalDate;
import java.util.List;


public interface Inntekt {

    LocalDate getStartdatoOpptjeningsperiode();

    LocalDate getSluttdatoOpptjeningsperiode();

    Float getAntall();

    String getOpptjeningsland();

    String getKalendermaaned();

    String getTypeArbeidsforhold();

    List<? extends Avvik> getAvvikList();

    boolean hasAvvik();
}
