package no.nav.identpool.batch.tps.xml.service;

import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.identpool.batch.tps.xml.TpsServiceRutine;

public class Personopplysning extends TpsServiceRutine {

    @JsonProperty("fnr")
    private final String fnr;

    public Personopplysning(String fnr) {
        super(ServiceRutinenavn.HENT_PERSONOPPLYSNIGNER, "F", "0");
        this.fnr = fnr;
    }
}