package no.nav.dolly.bestilling.etterlatte.dto;

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
public class VedtakResponseDTO {

    private HttpStatus status;
    private String message;

    public static Mono<VedtakResponseDTO> of(WebClientError.Description description) {

        return Mono.just(VedtakResponseDTO
                .builder()
                .status(description.getStatus())
                .message(description.getMessage())
                .build());
    }
}
