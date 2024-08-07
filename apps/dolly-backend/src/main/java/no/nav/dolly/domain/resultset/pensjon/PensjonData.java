package no.nav.dolly.domain.resultset.pensjon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PensjonData {

    @Schema(description = "Inntekt i pensjonsopptjeningsregister (POPP)")
    private PoppInntekt inntekt;

    @Schema(description = "Data for tjenestepensjon (TP)")
    private List<TpOrdning> tp;

    @Schema(description = "Data for pensjonsavtale")
    private List<Pensjonsavtale> pensjonsavtale;

    @Schema(description = "Data for alderspensjon (AP)")
    private Alderspensjon alderspensjon;

    @Schema(description = "Data for uføretrygd (UT)")
    private Uforetrygd uforetrygd;

    @JsonIgnore
    public boolean hasInntekt() {
        return nonNull(inntekt);
    }

    @JsonIgnore
    public boolean hasTp() {
        return !getTp().isEmpty();
    }

    @JsonIgnore
    public boolean hasAlderspensjon() {
        return nonNull(alderspensjon);
    }

    @JsonIgnore
    public boolean hasUforetrygd() {
        return nonNull(uforetrygd);
    }

    @JsonIgnore
    public boolean hasPensjonsavtale() {

        return !getPensjonsavtale().isEmpty();
    }

    public List<Pensjonsavtale> getPensjonsavtale() {

        if (isNull(pensjonsavtale)) {
            pensjonsavtale = new ArrayList<>();
        }
        return pensjonsavtale;
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
        @Field(type = FieldType.Date, format = DateFormat.basic_date, pattern = "uuuu-MM-dd")
        private LocalDate datoInnmeldtYtelseFom;

        @Schema(
                description = "Dato iverksatt fom")
        @Field(type = FieldType.Date, format = DateFormat.basic_date, pattern = "uuuu-MM-dd")
        private LocalDate datoYtelseIverksattFom;

        @Schema(description = "Dato iverksatt tom")
        @Field(type = FieldType.Date, format = DateFormat.basic_date, pattern = "uuuu-MM-dd")
        private LocalDate datoYtelseIverksattTom;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pensjonsavtale {
        public enum AvtaleKategori {
            NONE, UNKNOWN, INDIVIDUELL_ORDNING, PRIVAT_AFP,
            PRIVAT_TJENESTEPENSJON, OFFENTLIG_TJENESTEPENSJON, FOLKETRYGD
        }

        private String produktBetegnelse;
        private AvtaleKategori avtaleKategori;
        private Integer startAlderAar;
        private Integer sluttAlderAar;
        private List<OpprettUtbetalingsperiodeDTO> utbetalingsperioder;

        public List<OpprettUtbetalingsperiodeDTO> getUtbetalingsperioder() {

            if (isNull(utbetalingsperioder)) {
                utbetalingsperioder = new ArrayList<>();
            }
            return utbetalingsperioder;
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class OpprettUtbetalingsperiodeDTO {
            private Integer startAlderAar;
            private Integer startAlderMaaneder;
            private Integer sluttAlderAar;
            private Integer sluttAlderMaaneder;
            private Integer aarligUtbetaling;
            private Integer grad;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Alderspensjon {

        @Field(type = FieldType.Date, format = DateFormat.basic_date, pattern = "uuuu-MM-dd")
        private LocalDate iverksettelsesdato;

        @Field(type = FieldType.Date, format = DateFormat.basic_date, pattern = "uuuu-MM-dd")
        private LocalDate kravFremsattDato;

        private String saksbehandler;
        private String attesterer;
        private String navEnhetId;

        @Schema
        private Integer uttaksgrad;

        private Boolean soknad;

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

        @Field(type = FieldType.Date, format = DateFormat.basic_date, pattern = "uuuu-MM-dd")
        private LocalDate kravFremsattDato;
        @Field(type = FieldType.Date, format = DateFormat.basic_date, pattern = "uuuu-MM-dd")
        private LocalDate onsketVirkningsDato;
        @Field(type = FieldType.Date, format = DateFormat.basic_date, pattern = "uuuu-MM-dd")
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
        @Field(type = FieldType.Date, format = DateFormat.basic_date, pattern = "uuuu-MM-dd")
        private LocalDate datoFom;
        @Field(type = FieldType.Date, format = DateFormat.basic_date, pattern = "uuuu-MM-dd")
        private LocalDate datoTom;
        private InntektType inntektType;
        private Integer belop;
    }

    public enum UforeType {UNGUFOR, GIFT, ENSLIG}

    public enum BarnetilleggType {FELLESBARN, SAERKULLSBARN}

    public enum InntektType {ARBEIDSINNTEKT, NAERINGSINNTEKT, PENSJON_FRA_UTLANDET, UTENLANDS_INNTEKT, ANDRE_PENSJONER_OG_YTELSER}
}
