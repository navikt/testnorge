package no.nav.registre.aareg.consumer.rs.responses.contents;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
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
