package no.nav.dolly.bestilling.pensjonforvalter.domain;

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
public class PensjonsavtaleRequest {

    public enum AvtaleKategori {
        NONE, UNKNOWN, INDIVIDUELL_ORDNING, PRIVAT_AFP,
        PRIVAT_TJENESTEPENSJON, OFFENTLIG_TJENESTEPENSJON, FOLKETRYGD
    }

    private String ident;
    private String produktBetegnelse;
    private AvtaleKategori avtaleKategori;
    private List<OpprettUtbetalingsperiodeDTO>  utbetalingsperioder;
    private List<String> miljoer;

    public List<OpprettUtbetalingsperiodeDTO> getUtbetalingsperioder() {

        if (isNull(utbetalingsperioder)) {
            utbetalingsperioder = new ArrayList<>();
        }
        return utbetalingsperioder;
    }

    public List<String> getMiljoer() {

        if (isNull(miljoer)) {
            miljoer = new ArrayList<>();
        }
        return miljoer;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OpprettUtbetalingsperiodeDTO {
        private Integer startAlderAar;
        private Integer startAlderMaaned;
        private Integer sluttAlderAar;
        private Integer sluttAlderMaaned;
        private Integer aarligUtbetaling;
    }
}
