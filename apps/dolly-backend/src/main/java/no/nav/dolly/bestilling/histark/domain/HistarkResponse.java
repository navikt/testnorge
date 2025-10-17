package no.nav.dolly.bestilling.histark.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistarkResponse {

    private String dokument;
    private String id;
    private HttpStatus status;
    private String feilmelding;

    public static Mono<HistarkResponse> of(WebClientError.Description description) {
        return Mono.just(HistarkResponse
                .builder()
                .status(description.getStatus())
                .feilmelding(description.getMessage())
                .build());
    }

    public boolean isOk() {

        return isBlank(feilmelding);
    }
}
