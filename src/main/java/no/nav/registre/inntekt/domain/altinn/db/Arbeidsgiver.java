package no.nav.registre.inntekt.domain.altinn.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Arbeidsgiver {
    @JsonProperty
    private List<Inntektsmelding> inntektsmeldinger;
    @JsonProperty
    private Long id;
    @JsonProperty
    private String virksomhetsnummer;
    @JsonProperty
    private String kontaktinformasjonNavn;
    @JsonProperty
    private String telefonnummer;
}
