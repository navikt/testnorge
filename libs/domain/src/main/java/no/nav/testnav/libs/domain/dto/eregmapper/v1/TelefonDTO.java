package no.nav.testnav.libs.domain.dto.eregmapper.v1;

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
public class TelefonDTO {
    @JsonProperty
    private String mobil;
    @JsonProperty
    private String fax;
    @JsonProperty
    private String fast;
}
