package no.nav.dolly.domain.projection;

import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Bruker;

import java.time.LocalDate;
import java.util.Set;

public interface TestgruppeUtenIdenter {

    Long getId();

    String getNavn();

    String getHensikt();

    Bruker getOpprettetAv();

    Bruker getSistEndretAv();

    LocalDate getDatoEndret();

    Set<Bruker> getFavorisertAv();

    Set<Bestilling> getBestillinger();

    Boolean getErLaast();

    String getLaastBeskrivelse();

    String getTags();
}

