package no.nav.dolly.bestilling.pdldata.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PdlResponse {

    private String ident;
    private String jsonNode;
    private HttpStatus status;
    private String feilmelding;

    public static Mono<PdlResponse> of(WebClientError.Description description) {
        return Mono.just(PdlResponse
                .builder()
                .status(description.getStatus())
                .feilmelding(description.getMessage())
                .build());
    }
}