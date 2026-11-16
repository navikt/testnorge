package no.nav.dolly.bestilling.tagshendelseslager.dto;

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
public class TagsOpprettingResponse {

    private HttpStatus status;

    private String message;
    private List<Detaljert> details;

    public static Mono<TagsOpprettingResponse> of(WebClientError.Description description) {
        return Mono.just(TagsOpprettingResponse
                .builder()
                .status(description.getStatus())
                .message(description.getMessage())
                .build());
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Detaljert {

        private String name;
        private String message;
    }
}
