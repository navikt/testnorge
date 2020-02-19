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
public class Doctor {

    @JsonProperty("fornavn")
    private String firstName;
    @JsonProperty("mellomnavn")
    private String middleName;
    @JsonProperty("etternavn")
    private String lastName;
    @JsonProperty("fnr")
    private String fnr;
    @JsonProperty("her_id")
    private String herId;
    @JsonProperty("hpr_id")
    private String hprId;
    @JsonProperty("epost")
    private String email;
    @JsonProperty("tlf")
    private String tlf;
    @JsonProperty("legekontor")
    private Legekontor legekontor;

}
