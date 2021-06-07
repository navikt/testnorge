package no.nav.registre.testnorge.libs.dto.dokarkiv.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
public class Bruker {
    @JsonProperty
    private String id;
    @JsonProperty
    private String idType;
}
