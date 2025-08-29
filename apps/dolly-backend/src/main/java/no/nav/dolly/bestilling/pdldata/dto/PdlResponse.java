package no.nav.dolly.bestilling.pdldata.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PdlResponse {

    private static final String FINNES_IKKE = "finnes ikke";

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

    @JsonIgnore
    public boolean isFinnesIkke() {

        return isNotBlank(feilmelding) && feilmelding.contains(FINNES_IKKE);
    }
}