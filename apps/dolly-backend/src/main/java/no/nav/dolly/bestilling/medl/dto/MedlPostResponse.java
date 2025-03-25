package no.nav.dolly.bestilling.medl.dto;

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
public class MedlPostResponse {

    private HttpStatus status;
    private String melding;

    public static Mono<MedlPostResponse> of(WebClientError.Description description) {
        return Mono.just(MedlPostResponse.builder()
                .status(description.getStatus())
                .melding(description.getMessage())
                .build());
    }

}
