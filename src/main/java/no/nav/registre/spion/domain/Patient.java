package no.nav.registre.spion.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @JsonProperty("fornavn")
    private String firstName;
    @JsonProperty("mellomnavn")
    private String middleName;
    @JsonProperty("etternavn")
    private String lastName;
    @JsonProperty("fnr")
    private String fnr;
    @JsonProperty("tlf")
    private String tlf;
    @JsonProperty("fastlege")
    private String fastlege;
    @JsonProperty("nav_kontor")
    private String navKontor;
    @JsonProperty("arbeidsgiver")
    private Arbeidsgiver arbeidsgiver;

}