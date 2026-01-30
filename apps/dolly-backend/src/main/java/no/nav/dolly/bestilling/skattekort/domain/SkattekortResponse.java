package no.nav.dolly.bestilling.skattekort.domain;

import com.fasterxml.jackson.databind.JsonNode;
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
public class SkattekortResponse {

    private HttpStatus status;
    private String feilmelding;
    private JsonNode body;

    public boolean isOK() {
        return status.is2xxSuccessful();
    }

    public static Mono<SkattekortResponse> of(WebClientError.Description description) {
        return Mono.just(SkattekortResponse
                .builder()
                .status(description.getStatus())
                .feilmelding(description.getMessage())
                .build());
    }
}
