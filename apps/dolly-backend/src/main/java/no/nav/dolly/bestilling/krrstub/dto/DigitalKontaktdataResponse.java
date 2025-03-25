package no.nav.dolly.bestilling.krrstub.dto;

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
public class DigitalKontaktdataResponse {

    private HttpStatus status;
    private String melding;

    public static Mono<DigitalKontaktdataResponse> of(WebClientError.Description description) {
        return Mono.just(DigitalKontaktdataResponse
                .builder()
                .status(description.getStatus())
                .melding(description.getMessage())
                .build());
    }

}
