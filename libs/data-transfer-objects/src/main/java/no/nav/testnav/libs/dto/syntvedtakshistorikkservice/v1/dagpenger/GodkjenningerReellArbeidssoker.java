package no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GodkjenningerReellArbeidssoker {
    @JsonProperty
    private String godkjentLokalArbeidssoker;

    @JsonProperty
    private String godkjentDeltidssoker;

    @JsonProperty
    private String godkjentUtdanning;
}
