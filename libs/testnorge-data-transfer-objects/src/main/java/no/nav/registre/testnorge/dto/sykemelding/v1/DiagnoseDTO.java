package no.nav.registre.testnorge.dto.sykemelding.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@EqualsAndHashCode
public class DiagnoseDTO {
    @JsonProperty
    private String dn;

    @JsonProperty
    private String s;

    @JsonProperty
    private String v;
}
