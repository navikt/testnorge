package no.nav.registre.orgnrservice.domain;

import lombok.Value;

import no.nav.registre.orgnrservice.repository.model.OrgnummerModel;

@Value
public class Organisasjon {

    String orgnummer;
    boolean ledig;

    public Organisasjon(String orgnummer, boolean ledig) {
        this.orgnummer = orgnummer;
        this.ledig = ledig;
    }

    public Organisasjon(OrgnummerModel model) {
        this.orgnummer = model.getOrgnummer();
        this.ledig = model.isLedig();
    }
}
