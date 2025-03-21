package no.nav.dolly.bestilling.arbeidssoekerregisteret.dto;

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
public class ArbeidssoekerregisteretResponse {

    private HttpStatus status;
    private String feilmelding;

    public static Mono<ArbeidssoekerregisteretResponse> of(WebClientError.Description description) {
        return Mono.just(ArbeidssoekerregisteretResponse
                .builder()
                .status(description.getStatus())
                .feilmelding(description.getMessage())
                .build());
    }

}
