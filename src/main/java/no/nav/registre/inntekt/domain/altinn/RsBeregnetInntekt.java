package no.nav.registre.inntekt.domain.altinn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class RsBeregnetInntekt {
    @JsonProperty
    private Double beloep;
    @JsonProperty
    private String aarsakVedEndring;
}
