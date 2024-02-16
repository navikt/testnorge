package no.nav.testnav.libs.dto.inntektsmeldingservice.v1.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InntektDokumentResponse {

    String journalpostId;
    String dokumentInfoId;
    String xml;
}
