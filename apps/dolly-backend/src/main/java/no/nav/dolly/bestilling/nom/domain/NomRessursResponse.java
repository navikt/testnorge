package no.nav.dolly.bestilling.nom.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NomRessursResponse {

    private HttpStatus status;
    private String melding;

    private String fid;
    private String navident;
    private Navn navn;

    private String personident;
    private String sektor;
    private LocalDate sluttDato;
    private LocalDate startDato;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Navn {
        private String etternavn;
        private String fornavn;
        private String mellomnavn;
    }

    public static NomRessursResponse of(WebClientError.Description description) {

        return NomRessursResponse.builder()
                .status(description.getStatus())
                .melding(description.getMessage())
                .build();
    }
}
