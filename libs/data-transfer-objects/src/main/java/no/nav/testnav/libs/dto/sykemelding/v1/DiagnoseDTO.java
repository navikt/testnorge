package no.nav.testnav.libs.dto.sykemelding.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DiagnoseDTO {
    @JsonProperty
    private String diagnose;

    @JsonProperty
    private String system;

    @JsonProperty
    private String diagnosekode;
}
