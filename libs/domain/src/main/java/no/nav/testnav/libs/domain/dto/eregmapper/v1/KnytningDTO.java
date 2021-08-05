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
public class KnytningDTO {
    @JsonProperty
    private String type = "BEDRNSSY";
    @JsonProperty
    private String ansvarsandel = "";
    @JsonProperty
    private String fratreden = "";
    @JsonProperty(required = true)
    private String orgnr;
    @JsonProperty
    private String valgtAv = "";
    @JsonProperty
    private String korrektOrgNr = "";
}
