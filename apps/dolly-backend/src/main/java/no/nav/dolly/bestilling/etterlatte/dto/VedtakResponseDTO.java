package no.nav.dolly.bestilling.etterlatte.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import reactor.core.publisher.Mono;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VedtakResponseDTO {

    private Integer status;
    private String noekkel;

    public static Mono<VedtakResponseDTO> of(WebClientError.Description description) {
        return Mono.just(VedtakResponseDTO
                .builder()
                .status(description.getStatus().value())
                .build());
    }
}
