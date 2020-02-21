package no.nav.registre.spion.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
public class Arbeidsgiver {

    @JsonProperty("navn")
    private final String name;
    @JsonProperty("yrkesbetegnelse")
    private final String yrkesbetegnelse;
    @JsonProperty("stillingsprosent")
    private final String stillingsprosent;

}
