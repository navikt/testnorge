package no.nav.identpool.batch.tps.xml.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ServiceRutinenavn {

    HENT_PERSONOPPLYSNIGNER("FS03-FDNUMMER-PERSDATA-O");

    private String value;
}