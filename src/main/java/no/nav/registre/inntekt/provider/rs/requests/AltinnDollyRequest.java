package no.nav.registre.inntekt.provider.rs.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.nav.registre.inntekt.domain.altinn.RsAltinnInntektInfo;
import no.nav.registre.inntekt.domain.dokmot.rs.RsJoarkMetadata;

import java.util.List;

@Value
@NoArgsConstructor(force = true)
public class AltinnDollyRequest {
    @JsonProperty
    private String miljoe;
    @JsonProperty
    private String arbeidstakerFnr;
    @JsonProperty
    private RsJoarkMetadata joarkMetadata;
    @JsonProperty
    private List<RsAltinnInntektInfo> inntekter;
}
