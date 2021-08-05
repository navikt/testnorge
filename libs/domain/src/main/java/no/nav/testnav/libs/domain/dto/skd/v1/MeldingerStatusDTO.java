package no.nav.testnav.libs.domain.dto.skd.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeldingerStatusDTO {
    @JsonProperty
    private int antallSendte;
    @JsonProperty
    private int antallFeilet;
    @JsonProperty
    private List<MeldingStatusDTO> statusFraFeilendeMeldinger;
}
