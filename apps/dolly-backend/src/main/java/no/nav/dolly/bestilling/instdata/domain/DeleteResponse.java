package no.nav.dolly.bestilling.instdata.domain;

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
public class DeleteResponse {

    private String ident;
    private HttpStatus status;
    private String error;

    public static Mono<DeleteResponse> of(WebClientError.Description description, String ident) {
        return Mono.just(DeleteResponse.builder()
                .ident(ident)
                .status(description.getStatus())
                .error(description.getMessage())
                .build());
    }

}
