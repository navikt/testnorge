package no.nav.dolly.bestilling.aareg.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import no.nav.dolly.bestilling.aareg.deserialization.AktoerTypeEnumDeserializer;

@JsonDeserialize(using = AktoerTypeEnumDeserializer.class)
public enum Aktoer {

    ORGANISASJON,
    PERSON,
}
