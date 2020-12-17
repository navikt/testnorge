package no.nav.registre.orgnrservice.domain;

import lombok.Value;

import no.nav.registre.orgnrservice.repository.model.OrganisasjonModel;

@Value
public class Organisasjon {

    String orgnummer;
    boolean ledig;

    public Organisasjon(String orgnummer, boolean ledig) {
        this.orgnummer = orgnummer;
        this.ledig = ledig;
    }

    public Organisasjon(OrganisasjonModel model) {
        this.orgnummer = model.getOrgnummer();
        this.ledig = model.isLedig();
    }
}
