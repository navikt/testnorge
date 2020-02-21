package no.nav.registre.spion.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
public class Doctor {

    @JsonProperty("fornavn")
    private final String firstName;
    @JsonProperty("mellomnavn")
    private final String middleName;
    @JsonProperty("etternavn")
    private final String lastName;
    @JsonProperty("fnr")
    private final String fnr;
    @JsonProperty("her_id")
    private final String herId;
    @JsonProperty("hpr_id")
    private final String hprId;
    @JsonProperty("epost")
    private final String email;
    @JsonProperty("tlf")
    private final String tlf;
    @JsonProperty("legekontor")
    private final Legekontor legekontor;

}
