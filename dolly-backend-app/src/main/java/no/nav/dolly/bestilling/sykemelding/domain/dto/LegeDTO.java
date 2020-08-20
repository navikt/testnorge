package no.nav.dolly.bestilling.sykemelding.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class LegeDTO {
    String fnr;
    String fornavn;
    String mellomnavn;
    String etternavn;
    String hprId;
}
