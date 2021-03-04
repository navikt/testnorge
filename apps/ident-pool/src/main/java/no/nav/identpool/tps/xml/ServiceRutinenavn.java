package no.nav.identpool.tps.xml;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ServiceRutinenavn {

    HENT_NAVNOPPLYSNINGER("FS03-FDLISTER-DISKNAVN-M");

    @JsonValue
    private final String value;
}