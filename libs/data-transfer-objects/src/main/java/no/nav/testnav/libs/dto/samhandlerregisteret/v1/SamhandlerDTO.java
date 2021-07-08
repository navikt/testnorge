package no.nav.testnav.libs.dto.samhandlerregisteret.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class SamhandlerDTO {

    @JsonProperty("samh_ident")
    private final List<IdentDTO> identer;

    @JsonProperty("samh_type_kode")
    private final String kode;
}