package no.nav.testnav.libs.domain.dto.skd.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeneringsOrdreDTO {
    @JsonProperty(required = true)
    private final Long avspillergruppeId;
    @JsonProperty(required = true)
    private final String miljoe;
    @JsonProperty(required = true)
    private final Map<String, Integer> antallMeldingerPerEndringskode;
}
