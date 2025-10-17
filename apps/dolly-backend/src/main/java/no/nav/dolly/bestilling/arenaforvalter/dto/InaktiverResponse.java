package no.nav.dolly.bestilling.arenaforvalter.dto;

import lombok.*;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString(callSuper = true)
public class InaktiverResponse extends ArenaResponse {

    @Builder
    public InaktiverResponse(HttpStatus status, String miljoe, String feilmelding) {
        super(status, miljoe, feilmelding);
    }

    public static Mono<InaktiverResponse> of(WebClientError.Description description) {
        return Mono.just(InaktiverResponse
                .builder()
                .status(description.getStatus())
                .feilmelding(description.getMessage())
                .build());
    }

}
