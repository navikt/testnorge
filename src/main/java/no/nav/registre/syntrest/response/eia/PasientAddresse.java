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
public class PasientAddresse {
    @JsonProperty
    String gate;
    @JsonProperty
    String postnummer;
    @JsonProperty
    String by;
    @JsonProperty
    Fylke fylke;
    @JsonProperty("land_kode")
    String landKode;
}
