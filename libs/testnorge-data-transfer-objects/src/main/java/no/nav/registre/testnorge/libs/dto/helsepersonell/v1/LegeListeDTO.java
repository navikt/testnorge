package no.nav.registre.testnorge.libs.dto.helsepersonell.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class LegeListeDTO {
    @JsonProperty
    private final List<LegeDTO> leger;
}
