package no.nav.dolly.bestilling.sykemelding.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.bestilling.sykemelding.domain.DetaljertSykemeldingRequest;
import no.nav.dolly.bestilling.sykemelding.domain.SyntSykemeldingRequest;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SykemeldingResponse {

    private HttpStatus status;
    private String avvik;
    private SyntSykemeldingRequest syntSykemeldingRequest;
    private DetaljertSykemeldingRequest detaljertSykemeldingRequest;
}
