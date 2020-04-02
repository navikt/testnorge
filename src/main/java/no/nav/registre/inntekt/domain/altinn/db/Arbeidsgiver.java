package no.nav.registre.inntekt.domain.altinn.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@NoArgsConstructor(force = true)
public class Arbeidsgiver {
    @JsonProperty
    List<Inntektsmelding> inntektsmeldinger;
    @JsonProperty
    Long id;
    @JsonProperty
    String virksomhetsnummer;
    @JsonProperty
    String kontaktinformasjonNavn;
    @JsonProperty
    String telefonnummer;
}
