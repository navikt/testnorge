package no.nav.registre.inntekt.provider.rs.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.registre.inntekt.domain.altinn.rs.RsInntektsmelding;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AltinnDollyFullMeldingRequest {

    @JsonProperty
    private String miljoe;
    @JsonProperty
    private String arbeidstakerFnr;
    @JsonProperty
    private List<RsInntektsmelding> inntekter;
}
