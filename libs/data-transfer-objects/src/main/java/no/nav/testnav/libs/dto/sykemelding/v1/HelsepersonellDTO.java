package no.nav.testnav.libs.dto.sykemelding.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class HelsepersonellDTO {
    @JsonProperty
    private String ident;
    @JsonProperty
    private String fornavn;
    @JsonProperty
    private String mellomnavn;
    @JsonProperty
    private String etternavn;
    @JsonProperty
    private String hprId;
    @JsonProperty
    private String samhandlerType;
}
