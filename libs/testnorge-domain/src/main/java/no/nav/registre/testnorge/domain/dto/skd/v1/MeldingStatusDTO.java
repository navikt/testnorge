package no.nav.registre.testnorge.domain.dto.skd.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeldingStatusDTO {

    @JsonProperty(required = true)
    private final String foedselsnummer;

    @JsonProperty(required = true)
    private final Long sekvensnummer;

    @JsonProperty(required = true)
    private final String status;
}
