package no.nav.dolly.bestilling.yrkesskade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatusCode;
import reactor.core.publisher.Mono;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YrkesskadeResponseDTO {

    private HttpStatusCode status;
    private String melding;

    public static Mono<YrkesskadeResponseDTO> of(WebClientError.Description description) {
        return Mono.just(YrkesskadeResponseDTO
                .builder()
                .status(description.getStatus())
                .melding(description.getMessage())
                .build());
    }
}
