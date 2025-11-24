package no.nav.dolly.bestilling.sigrunstub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SigrunstubResponse {

    private HttpStatus status;
    private String melding;
    private String ident;

    List<OpprettelseTilbakemelding> opprettelseTilbakemeldingsListe;

    public static Mono<SigrunstubResponse> of(WebClientError.Description description, String ident) {
        return Mono.just(SigrunstubResponse
                .builder()
                .ident(ident)
                .status(description.getStatus())
                .melding(description.getMessage())
                .build());
    }

    public List<OpprettelseTilbakemelding> getOpprettelseTilbakemeldingsListe() {

        if (isNull(opprettelseTilbakemeldingsListe)) {
            opprettelseTilbakemeldingsListe = new ArrayList<>();
        }
        return opprettelseTilbakemeldingsListe;
    }

    public boolean isOK() {

        return status.is2xxSuccessful();
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OpprettelseTilbakemelding {

        private String inntektsaar;
        private String personident;
        private Integer status;

        private String message;

        public boolean isOK() {
            return nonNull(status) && status == 200;
        }

        public boolean isError() {
            return nonNull(status) && status != 200;
        }
    }
}