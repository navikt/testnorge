package no.nav.dolly.bestilling.skjermingsregister.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import reactor.core.publisher.Mono;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkjermingDataResponse {

    private String endretDato;
    private String etternavn;
    private String fornavn;
    private String opprettetDato;
    private String personident;
    private String skjermetFra;
    private String skjermetTil;

    @JsonIgnore
    private boolean eksistererIkke;

    private String error;

    public static Mono<SkjermingDataResponse> of(WebClientError.Description description) {
        return Mono.just(SkjermingDataResponse
                .builder()
                .error(description.getMessage())
                .build());
    }

}
