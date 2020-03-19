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
public class RsKontaktinformasjon {
    @JsonProperty
    private String kontaktinformasjonNavn;
    @JsonProperty
    private String telefonnummer;
}
