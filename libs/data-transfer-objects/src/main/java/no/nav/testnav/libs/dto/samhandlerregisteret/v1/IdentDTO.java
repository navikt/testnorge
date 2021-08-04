package no.nav.testnav.libs.dto.samhandlerregisteret.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class IdentDTO {
    @JsonProperty("ident")
    private final String ident;
    @JsonProperty("ident_type_kode")
    private final String identTypeKode;
}
