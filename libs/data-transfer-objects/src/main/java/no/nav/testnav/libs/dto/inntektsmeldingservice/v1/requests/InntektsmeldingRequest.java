package no.nav.testnav.libs.dto.inntektsmeldingservice.v1.requests;

import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.dto.dokarkiv.v1.RsJoarkMetadata;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.RsInntektsmeldingRequest;

import java.util.List;

@Data
@NoArgsConstructor
public class InntektsmeldingRequest {

    private String miljoe;
    private String arbeidstakerFnr;  // TODO: ident! (i v2)
    private RsJoarkMetadata joarkMetadata;
    private List<RsInntektsmeldingRequest> inntekter;
}
