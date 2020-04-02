package no.nav.registre.inntekt.domain.altinn.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@NoArgsConstructor(force = true)
public class Eier {
    @JsonProperty
    Long id;
    @JsonProperty
    String navn;
    @JsonProperty
    List<Inntektsmelding> inntektsmeldinger;
}
