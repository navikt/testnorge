package no.nav.registre.inntekt.domain.altinn.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
    private RsArbeidsgiverPrivat arbeidsgiverPrivat;
    @JsonProperty
    private RsArbeidsforhold arbeidsforhold;
}
