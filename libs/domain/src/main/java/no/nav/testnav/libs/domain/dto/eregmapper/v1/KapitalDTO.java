package no.nav.testnav.libs.domain.dto.eregmapper.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class KapitalDTO {
    @JsonProperty
    private String valuttakode;
    @JsonProperty
    private String kapital;
    @JsonProperty
    private String kapitalInnbetalt;
    @JsonProperty
    private String kapitalBundet;
    @JsonProperty
    private String fritekst;
}
