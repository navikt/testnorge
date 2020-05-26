package no.nav.registre.elsam.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Diagnose {

    @JsonProperty("dn")
    @JsonAlias("DN")
    private String dn;

    @JsonProperty("s")
    @JsonAlias("S")
    private String s;

    @JsonProperty("v")
    @JsonAlias("V")
    private String v;
}
