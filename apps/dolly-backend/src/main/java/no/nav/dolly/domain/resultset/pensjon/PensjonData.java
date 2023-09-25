package no.nav.dolly.domain.resultset.pensjon;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PensjonData {

    @Schema(description = "Inntekt i pensjonsopptjeningsregister (POPP)")
    private PoppInntekt inntekt;

    @Schema(description = "Data for tjenestepensjon (TP)")
    private List<TpOrdning> tp;

    @Schema(description = "Data for alderspensjon (AP)")
    private Alderspensjon alderspensjon;

    @Schema(description = "Data for uføretrygd (UT)")
    private Uforetrygd uforetrygd;

    public boolean hasInntekt() {
        return nonNull(inntekt);
    }

    public boolean hasTp() {
        return !tp.isEmpty();
    }

    public boolean hasAlderspensjon() {
        return nonNull(alderspensjon);
    }

    public boolean hasUforetrygd() {
        return nonNull(uforetrygd);
    }

    public List<TpOrdning> getTp() {

        if (isNull(tp)) {
            tp = new ArrayList<>();
        }
        return tp;
    }

    public enum TpYtelseType {
        ALDER,
        UFORE,
        GJENLEVENDE,
        BARN,
        AFP,
        UKJENT
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PoppInntekt {
        @Schema(description = "Fra og med år YYYY")
        private Integer fomAar;

        @Schema(description = "Til og med år YYYY")
        private Integer tomAar;

        @Schema(description = "Beløp i hele kroner per år (i dagens verdi)")
        private Integer belop;

        @Schema(description = "Når true reduseres tidligere års pensjon i forhold til dagens kroneverdi")
        private Boolean redusertMedGrunnbelop;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TpOrdning {
        @Schema(description = "Tjenestepensjons leverandør")
        private String ordning;

        @Schema(description = "Tjenestepensjons ytelser")
        private List<TpYtelse> ytelser;

        public List<TpYtelse> getYtelser() {
            if (ytelser == null) {
                ytelser = new ArrayList<>();
            }
            return ytelser;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TpYtelse {
        @Schema(
                description = "tjenestetype")
        private TpYtelseType type;

        @Schema(
                description = "Dato innmeldt ytelse fom, kan være tidligere eller samme som iverksatt fom dato.")
        private LocalDate datoInnmeldtYtelseFom;

        @Schema(
                description = "Dato iverksatt fom")
        private LocalDate datoYtelseIverksattFom;

        @Schema(description = "Dato iverksatt tom")
        private LocalDate datoYtelseIverksattTom;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Alderspensjon {
        @Schema
        private LocalDate iverksettelsesdato;

        @Schema
        private Integer uttaksgrad;

        @Schema
        private List<SkjemaRelasjon> relasjoner;

        public List<SkjemaRelasjon> getRelasjoner() {

            if (isNull(relasjoner)) {
                relasjoner = new ArrayList<>();
            }
            return relasjoner;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkjemaRelasjon {
        @Schema
        private Integer sumAvForvArbKapPenInntekt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Uforetrygd {

        private LocalDate kravFremsattDato;
        private LocalDate onsketVirkningsDato;
        private LocalDate uforetidspunkt;
        private Integer inntektForUforhet;
        private Integer uforegrad;
        private UforeType minimumInntektForUforhetType;
        private String saksbehandler;
        private String attesterer;
        private String navEnhetId;
        private Barnetillegg barnetilleggDetaljer;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Barnetillegg {
        private BarnetilleggType barnetilleggType;

        private List<ForventetInntekt> forventedeInntekterSoker;
        private List<ForventetInntekt> forventedeInntekterEP;

        public List<ForventetInntekt> getForventedeInntekterSoker() {

            if (isNull(forventedeInntekterSoker)) {
                forventedeInntekterSoker = new ArrayList<>();
            }
            return forventedeInntekterSoker;
        }

        public List<ForventetInntekt> getForventedeInntekterEP() {

            if (isNull(forventedeInntekterEP)) {
                forventedeInntekterEP = new ArrayList<>();
            }
            return forventedeInntekterEP;
        }
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
