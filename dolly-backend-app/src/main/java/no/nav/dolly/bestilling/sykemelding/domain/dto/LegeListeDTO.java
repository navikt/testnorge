package no.nav.dolly.bestilling.sykemelding.domain.dto;

import java.util.List;

import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
public class LegeListeDTO {
    List<LegeDTO> leger;
}

