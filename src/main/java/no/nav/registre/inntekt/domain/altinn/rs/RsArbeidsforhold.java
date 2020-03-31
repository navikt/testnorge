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
public class RsArbeidsforhold {
    @JsonProperty
    private String arbeidsforholdId;
    @JsonProperty
    private RsBeregnetInntekt beregnetInntekt;
}
