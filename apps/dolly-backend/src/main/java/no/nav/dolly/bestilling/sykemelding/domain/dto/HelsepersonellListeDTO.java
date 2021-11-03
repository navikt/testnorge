package no.nav.dolly.bestilling.sykemelding.domain.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class HelsepersonellListeDTO {
    List<HelsepersonellDTO> helsepersonell;
}

