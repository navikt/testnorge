package no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.dagpenger;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class GodkjenningerReellArbeidssoker {
    @JsonProperty
    private String godkjentLokalArbeidssoker;

    @JsonProperty
    private String godkjentDeltidssoker;

    @JsonProperty
    private String godkjentUtdanning;
}
