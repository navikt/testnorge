package no.nav.registre.syntrest.response.eia;

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
    @JsonProperty
    String navn;
    @JsonProperty("ereg_id")
    String eregId;
    @JsonProperty("her_id")
    long herId;
    @JsonProperty
    LegeAddresse addresse;
}
