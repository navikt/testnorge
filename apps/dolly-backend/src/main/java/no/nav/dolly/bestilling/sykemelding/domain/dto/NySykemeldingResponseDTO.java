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
public class NySykemeldingResponseDTO {

    private HttpStatus status;
    private String avvik;
    private NySykemeldingRequest nySykemeldingRequest;
    private String msgId;
    private String ident;

    public static Mono<NySykemeldingResponseDTO> of(WebClientError.Description description, String ident) {
        return Mono.just(NySykemeldingResponseDTO
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
    public static class NySykemeldingRequest {

        private String sykemeldingId;
        private NySykemeldingRequestDTO nySykemeldingRequestDTO;
    }
}
