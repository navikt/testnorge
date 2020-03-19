package no.nav.registre.inntekt.domain.altinn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class RsArbeidsgiver {
    @JsonProperty
    private String virksomhetsnummer;
    @JsonProperty
    private RsKontaktinformasjon kontaktinformasjon;
}
