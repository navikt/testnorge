package no.nav.dolly.bestilling.sykemelding.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.bestilling.sykemelding.domain.DetaljertSykemeldingRequest;
import org.springframework.http.HttpStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SykemeldingResponse {

    private HttpStatus status;
    private String avvik;
    private SykemeldingRequest sykemeldingRequest;
    private String msgId;
    private String ident;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class SykemeldingRequest {

        private String sykemeldingId;
        private DetaljertSykemeldingRequest detaljertSykemeldingRequest;
    }
}
