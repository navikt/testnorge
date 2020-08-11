package no.nav.dolly.bestilling.sykemelding.domain.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class LegeListeDTO {
    @JsonProperty
    private final List<LegeDTO> leger;
}

