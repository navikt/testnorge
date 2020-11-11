package no.nav.dolly.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.nav.registre.testnorge.libs.dto.tilbakemeldingapi.v1.Rating;

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