package no.nav.registre.inntekt.domain.altinn.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Eier {
    @JsonProperty
    private Long id;
    @JsonProperty
    private String navn;
    @JsonProperty
    private List<Inntektsmelding> inntektsmeldinger;
}
