package no.nav.registre.syntrest.domain.eia;

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
    @JsonProperty
    String navn;
    @JsonProperty
    String yrkesbetegnelse;
    @JsonProperty
    int stillingsprosent;
}
