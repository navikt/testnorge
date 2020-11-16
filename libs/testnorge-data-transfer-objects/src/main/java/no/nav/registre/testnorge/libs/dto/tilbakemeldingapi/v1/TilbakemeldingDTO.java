package no.nav.registre.testnorge.libs.dto.tilbakemeldingapi.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class TilbakemeldingDTO {
    @JsonProperty(required = true)
    String title;
    @JsonProperty(required = true)
    String message;
    @JsonProperty
    Rating rating;
    @JsonProperty
    Boolean isAnonym;
}
