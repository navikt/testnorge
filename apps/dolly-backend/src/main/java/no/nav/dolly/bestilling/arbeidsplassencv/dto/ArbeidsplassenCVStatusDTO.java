package no.nav.dolly.bestilling.arbeidsplassencv.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

@Data
@Builder
@AllArgsConstructor
public class ArbeidsplassenCVStatusDTO {

    private HttpStatus status;
    private String feilmelding;
    private PAMCVDTO arbeidsplassenCV;
    private JsonNode jsonNode;
    private String uuid;

    public static Mono<ArbeidsplassenCVStatusDTO> of(WebClientError.Description description, String uuid) {
        return Mono.just(ArbeidsplassenCVStatusDTO
                .builder()
                .status(description.getStatus())
                .feilmelding(description.getMessage())
                .uuid(uuid)
                .build());
    }

}
