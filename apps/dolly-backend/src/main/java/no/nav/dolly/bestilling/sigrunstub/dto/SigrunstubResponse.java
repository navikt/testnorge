package no.nav.dolly.bestilling.sigrunstub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SigrunstubResponse {

    private HttpStatus errorStatus;
    private String melding;
    private String ident;

    List<OpprettelseTilbakemelding> opprettelseTilbakemeldingsListe;

    public List<OpprettelseTilbakemelding> getOpprettelseTilbakemeldingsListe() {

        if (isNull(opprettelseTilbakemeldingsListe)) {
            opprettelseTilbakemeldingsListe = new ArrayList<>();
        }
        return opprettelseTilbakemeldingsListe;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OpprettelseTilbakemelding {

        private String message;
        private String personident;
        private Integer status;

        public boolean isOK() {
            return nonNull(status) && status == 200;
        }

        public boolean isError() {
            return nonNull(status) && status != 200;
        }
    }
}