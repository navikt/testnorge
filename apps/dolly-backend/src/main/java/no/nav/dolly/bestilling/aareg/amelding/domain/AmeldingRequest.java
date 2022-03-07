package no.nav.dolly.bestilling.aareg.amelding.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.testnav.libs.dto.ameldingservice.v1.AMeldingDTO;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmeldingRequest {

    private AMeldingDTO aMeldingDTO;
    private String miljoe;
}
