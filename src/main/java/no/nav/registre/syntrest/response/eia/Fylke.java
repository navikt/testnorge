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
public class Fylke {
    @JsonProperty
    String dn;
    @JsonProperty
    String v;
}
