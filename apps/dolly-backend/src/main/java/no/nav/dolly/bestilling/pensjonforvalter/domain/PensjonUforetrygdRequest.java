package no.nav.dolly.bestilling.pensjonforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PensjonUforetrygdRequest implements PensjonTransaksjonId {

    private String fnr;
    private LocalDate kravFremsattDato;
    private LocalDate onsketVirkningsDato;
    private LocalDate uforetidspunkt;
    private Integer inntektForUforhet;
    private Integer inntektEtterUforhet;
    private Integer uforegrad;
    private UforeType minimumInntektForUforhetType;
    private String saksbehandler;
    private String attesterer;
    private String navEnhetId;
    private Barnetillegg barnetilleggDetaljer;
    private List<String> miljoer;

    public List<String> getMiljoer() {
        if (isNull(miljoer)) {
            miljoer = new ArrayList<>();
        }
        return miljoer;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Barnetillegg {
        private BarnetilleggType barnetilleggType;

        private List<ForventetInntekt> forventedeInntekterSoker;
        private List<ForventetInntekt> forventedeInntekterEP;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ForventetInntekt {
        private LocalDate datoFom;
        private LocalDate datoTom;
        private InntektType inntektType;
        private Integer belop;
    }

    public enum UforeType {UNGUFOR, GIFT, ENSLIG}

    public enum BarnetilleggType {FELLESBARN, SAERKULLSBARN}

    public enum InntektType {ARBEIDSINNTEKT, NAERINGSINNTEKT, PENSJON_FRA_UTLANDET, UTENLANDS_INNTEKT, ANDRE_PENSJONER_OG_YTELSER}
}

