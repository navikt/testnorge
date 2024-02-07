package no.nav.testnav.libs.dto.inntektsmeldingservice.v1.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SyntetiseringsRequest {

    private Long avspillergruppeId;
    private String miljoe;
}
