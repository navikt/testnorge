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
