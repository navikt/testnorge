package no.nav.testnav.apps.tenorsearchservice.domain;

import tools.jackson.databind.JsonNode;
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
public class TenorResponse {

    private HttpStatus status;
    private JsonNode data;
    private String query;
    private String error;

    public static Mono<TenorResponse> of(WebClientError.Description description, String query) {
        return Mono.just(TenorResponse
                .builder()
                .status(description.getStatus())
                .error(description.getMessage())
                .query(query)
                .build());
    }

}