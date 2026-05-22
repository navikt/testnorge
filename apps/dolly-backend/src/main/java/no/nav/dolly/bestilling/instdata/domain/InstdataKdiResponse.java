package no.nav.dolly.bestilling.instdata.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

@Getter
@Setter
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
