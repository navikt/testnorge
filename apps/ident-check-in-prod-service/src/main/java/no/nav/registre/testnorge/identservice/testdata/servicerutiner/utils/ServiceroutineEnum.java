package no.nav.registre.testnorge.identservice.testdata.servicerutiner.utils;

import lombok.Getter;

public enum ServiceroutineEnum {

    FNR("fnr"), ENVIRONMENT("environment"), AKSJONSKODE("aksjonsKode"), AKSJONSDATO("aksjonsDato");

    ServiceroutineEnum(String name) {
        this.name = name;
    }

    @Getter
    private String name;
}
