package no.nav.registre.spion.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
public class Patient {

    @JsonProperty("fornavn")
    private final  String firstName;
    @JsonProperty("mellomnavn")
    private final String middleName;
    @JsonProperty("etternavn")
    private final String lastName;
    @JsonProperty("fnr")
    private final String fnr;
    @JsonProperty("tlf")
    private final String tlf;
    @JsonProperty("fastlege")
    private final String fastlege;
    @JsonProperty("nav_kontor")
    private final String navKontor;
    @JsonProperty("arbeidsgiver")
    private final Arbeidsgiver arbeidsgiver;

}