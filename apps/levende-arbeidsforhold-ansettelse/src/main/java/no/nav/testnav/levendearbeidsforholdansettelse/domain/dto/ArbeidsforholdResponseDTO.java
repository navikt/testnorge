package no.nav.testnav.levendearbeidsforholdansettelse.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatusCode;
import reactor.core.publisher.Mono;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArbeidsforholdResponseDTO {

    private HttpStatusCode statusCode;
    private String payload;
    private String feilmelding;

    public static Mono<ArbeidsforholdResponseDTO> of(WebClientError.Description description) {
        return Mono.just(ArbeidsforholdResponseDTO
                .builder()
                .statusCode(description.getStatus())
                .feilmelding(description.getMessage())
                .build());
    }

}
