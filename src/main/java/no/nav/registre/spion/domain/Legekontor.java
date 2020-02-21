package no.nav.registre.spion.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
public class Legekontor {

    @JsonProperty("navn")
    private String name;
    @JsonProperty("ereg_id")
    private String eregId;
    @JsonProperty("her_id")
    private String herId;
    @JsonProperty("adresse")
    private Address address;

}
