package no.nav.dolly.bestilling.sigrunstub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SigrunResponse {

    private List<ResponseElement> opprettelseTilbakemeldingsListe;

    public List<ResponseElement> getOpprettelseTilbakemeldingsListe() {

        if (isNull(opprettelseTilbakemeldingsListe)) {
            opprettelseTilbakemeldingsListe = new ArrayList<>();
        }
        return opprettelseTilbakemeldingsListe;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseElement {

        private String personident;
        private Integer status;
        private String message;
    }
}