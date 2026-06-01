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
public class InstdataKdiResponse {

    private HttpStatus status;
    private String ident;
    private InstdataKdiDTO instdataKdi;
    private String feilmelding;

    private String environment;

    public static Mono<InstdataKdiResponse> of(WebClientError.Description description, String ident, String miljoe) {

        return Mono.just(InstdataKdiResponse
                .builder()
                .ident(ident)
                .status(description.getStatus())
                .feilmelding(description.getMessage())
                .environment(miljoe)
                .build());
    }
}
