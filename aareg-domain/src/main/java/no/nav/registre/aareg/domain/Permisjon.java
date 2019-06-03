package no.nav.registre.aareg.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Permisjon {

    @JsonProperty("permisjonOgPermittering")
    private String permisjonOgPermittering;

    @JsonProperty("permisjonsId")
    private String permisjonsId;

    @JsonProperty("permisjonsPeriode")
    private Map<String, String> permisjonsPeriode;

    @JsonProperty("permisjonsprosent")
    private Double permisjonsprosent;
}
