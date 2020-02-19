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
public class Arbeidsgiver {

    @JsonProperty("navn")
    private String name;
    @JsonProperty("yrkesbetegnelse")
    private String yrkesbetegnelse;
    @JsonProperty("stillingsprosent")
    private String stillingsprosent;

}
