package no.nav.registre.inntekt.provider.rs.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.inntekt.domain.altinn.AltinnInntektInfo;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AltinnDollyRequest {
    @JsonProperty
    private String miljoe;
    @JsonProperty
    private String arbeidstakerFnr;
    @JsonProperty
    private List<AltinnInntektInfo> inntekter;
}
