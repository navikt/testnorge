package no.nav.dolly.bestilling.tagshendelseslager.dto;

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
public class HendelselagerResponse {

    private HttpStatus status;
    private String body;
    private String feilmelding;

    public static Mono<HendelselagerResponse> of(WebClientError.Description description) {
        return Mono.just(HendelselagerResponse
                .builder()
                .status(description.getStatus())
                .feilmelding(description.getMessage())
                .build());
    }
}
