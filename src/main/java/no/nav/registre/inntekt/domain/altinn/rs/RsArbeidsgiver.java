package no.nav.registre.inntekt.domain.altinn.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class RsArbeidsgiver {
    @JsonProperty
    private String virksomhetsnummer;
    @JsonProperty
    private RsKontaktinformasjon kontaktinformasjon;
}
