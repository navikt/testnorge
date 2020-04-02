package no.nav.registre.inntekt.domain.altinn.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@NoArgsConstructor(force = true)
public class Eier {
    @JsonProperty
    private Long id;
    @JsonProperty
    private String navn;
    @JsonProperty
    private List<Inntektsmelding> inntektsmeldinger;
}
