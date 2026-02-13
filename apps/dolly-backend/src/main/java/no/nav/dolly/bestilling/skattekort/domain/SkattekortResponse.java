package no.nav.dolly.bestilling.skattekort.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkattekortResponse {

    private HttpStatus status;
    private String feilmelding;
    private List<SkattekortDTO> skattekort;

    public static Mono<SkattekortResponse> of(WebClientError.Description description) {
        return Mono.just(SkattekortResponse
                .builder()
                .status(description.getStatus())
                .feilmelding(description.getMessage())
                .build());
    }
}
