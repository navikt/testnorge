package no.nav.testnav.libs.dto.helsepersonell.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class HelsepersonellListeDTO {
    @JsonProperty
    private final List<HelsepersonellDTO> helsepersonell;
}
