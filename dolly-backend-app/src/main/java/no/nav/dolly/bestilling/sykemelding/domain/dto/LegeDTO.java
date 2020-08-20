package no.nav.dolly.bestilling.sykemelding.domain.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LegeDTO {
    String fnr;
    String fornavn;
    String mellomnavn;
    String etternavn;
    String hprId;
}
