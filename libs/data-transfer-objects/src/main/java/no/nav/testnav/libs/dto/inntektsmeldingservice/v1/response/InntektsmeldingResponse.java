package no.nav.testnav.libs.dto.inntektsmeldingservice.v1.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InntektsmeldingResponse {

    private String fnr;
    private List<InntektDokumentResponse> dokumenter;
}
