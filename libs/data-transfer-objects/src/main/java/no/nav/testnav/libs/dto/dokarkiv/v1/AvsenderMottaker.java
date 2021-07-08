package no.nav.testnav.libs.dto.dokarkiv.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class AvsenderMottaker {
    @JsonProperty
    private String id;
    @JsonProperty
    private String idType;
    @JsonProperty
    private String navn;
}
