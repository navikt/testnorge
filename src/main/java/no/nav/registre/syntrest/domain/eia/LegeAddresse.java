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
public class LegeAddresse {
    @JsonProperty
    String gate;
    @JsonProperty
    String postnummer;
    @JsonProperty
    String by;
}
