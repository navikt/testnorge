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
    @Builder.Default
    @JsonProperty
    private String type = "BEDRNSSY";
    @Builder.Default
    @JsonProperty
    private String ansvarsandel = "";
    @Builder.Default
    @JsonProperty
    private String fratreden = "";
    @JsonProperty(required = true)
    private String orgnr;
    @Builder.Default
    @JsonProperty
    private String valgtAv = "";
    @Builder.Default
    @JsonProperty
    private String korrektOrgNr = "";
}
