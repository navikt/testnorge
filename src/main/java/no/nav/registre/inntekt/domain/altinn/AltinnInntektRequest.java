package no.nav.registre.inntekt.domain.altinn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class AltinnInntektRequest {
    @JsonProperty
    private String ytelse;
    @JsonProperty
    private String aarsakTilInnsending;
    @JsonProperty
    private String arbeidstakerFnr;
    @JsonProperty
    private RsAvsendersystem avsendersystem;
    @JsonProperty
    private RsArbeidsgiver arbeidsgiver;
    @JsonProperty
    private RsArbeidsforhold arbeidsforhold;
}
