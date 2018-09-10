package no.nav.identpool.batch.tps.xml;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ServiceRutinenavn {

    HENT_PERSONOPPLYSNIGNER("FS03-FDNUMMER-PERSDATA-O");

    @JsonValue
    private String value;
}