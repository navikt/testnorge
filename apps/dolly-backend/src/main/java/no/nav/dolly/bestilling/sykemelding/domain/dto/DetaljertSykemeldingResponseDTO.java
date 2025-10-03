package no.nav.dolly.bestilling.sykemelding.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetaljertSykemeldingResponseDTO {

    private HttpStatus status;
    private String avvik;
    private SykemeldingRequest sykemeldingRequest;
    private String msgId;
    private String ident;

    public static Mono<DetaljertSykemeldingResponseDTO> of(WebClientError.Description description, String ident) {
        return Mono.just(DetaljertSykemeldingResponseDTO
                .builder()
                .ident(ident)
                .status(description.getStatus())
                .avvik(description.getMessage())
                .build());
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class SykemeldingRequest {

        private String sykemeldingId;
        private DetaljertSykemeldingRequestDTO detaljertSykemeldingRequestDTO;
    }
}
