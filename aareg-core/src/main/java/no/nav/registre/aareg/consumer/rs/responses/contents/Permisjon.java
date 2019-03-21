package no.nav.registre.aareg.consumer.rs.responses.contents;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
