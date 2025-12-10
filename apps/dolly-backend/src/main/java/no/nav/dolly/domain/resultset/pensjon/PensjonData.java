package no.nav.dolly.domain.resultset.pensjon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isNotTrue;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PensjonData {

    @Schema(description = "Inntekt i pensjonsopptjeningsregister (POPP)")
    private PoppInntekt inntekt;

    @Schema(description = "Generert inntekt i pensjonsopptjeningsregister (POPP)")
    private PoppGenerertInntektWrapper generertInntekt;

    @Schema(description = "Data for tjenestepensjon (TP)")
    private List<TpOrdning> tp;

    @Schema(description = "Data for pensjonsavtale")
    private List<Pensjonsavtale> pensjonsavtale;

    @Schema(description = "Data for alderspensjon (AP)")
    private Alderspensjon alderspensjon;

    @Schema(description = "Data for endring av AP uttaksgrad")
    private AlderspensjonNyUtaksgrad alderspensjonNyUtaksgrad;

    @Schema(description = "Data for uføretrygd (UT)")
    private Uforetrygd uforetrygd;

    @Schema(description = "Data for AFP offentlig")
    private AfpOffentlig afpOffentlig;

    @JsonIgnore
    public boolean hasInntekt() {
        return nonNull(inntekt);
    }

    @JsonIgnore
    public boolean hasGenerertInntekt() {
        return nonNull(generertInntekt);
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
    public boolean hasNyUttaksgrad() {
        return nonNull(alderspensjonNyUtaksgrad);
    }

    @JsonIgnore
    public boolean hasUforetrygd() {
        return nonNull(uforetrygd);
    }

    @JsonIgnore
    public boolean hasPensjonsavtale() {

        return !getPensjonsavtale().isEmpty();
    }

    @JsonIgnore
    public boolean hasAfpOffentlig() {
        return nonNull(afpOffentlig);
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
        AFP,
        ALDER,
        BARN,
        BETINGET_TP,
        GJENLEVENDE,
        LIVSVARIG_AFP,
        OPPSATT_BTO_PEN,
        OVERGANGSTILLEGG,
        PAASLAGSPENSJON,
        SAERALDER,
        SAERALDERSPAASLAG,
        TIDLIGPENSJON,
        TIDLIGPEN_OVERGANG,
        UFORE,
        UKJENT
    }

    public enum UforeType {UNGUFOR, GIFT, ENSLIG}

    public enum BarnetilleggType {FELLESBARN, SAERKULLSBARN}

    public enum InntektType {ARBEIDSINNTEKT, NAERINGSINNTEKT, PENSJON_FRA_UTLANDET, UTENLANDS_INNTEKT, ANDRE_PENSJONER_OG_YTELSER}

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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PoppGenerertInntektWrapper {

        @Schema(description = "Verdier brukt til generering av inntekt")
        private PoppGenerer generer;

        @Schema(description = "Genererte verdier for POPP inntekt")
        private List<PoppGenerertInntekt> inntekter;

        public List<PoppGenerertInntekt> getInntekter() {
            if (inntekter == null) {
                inntekter = new ArrayList<>();
            }
            return inntekter;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PoppGenerer {

        @Schema(description = "Fra og med år YYYY")
        private Integer fomAar;

        @Schema(description = "Til og med år YYYY")
        private Integer tomAar;

        @Schema(description = "Gjennomsnittlig grunnbeløp (G) per år")
        private Float averageG;

        @Schema(description = "Gjennomsnittlig grunnbeløp (G) kan genereres til verdi under 1G")
        private Boolean tillatInntektUnder1G;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PoppGenerertInntekt {

        @Schema(description = "Gjeldende år")
        private Integer ar;

        @Schema(description = "Inntekt i hele kroner for året")
        private Integer inntekt;

        @Schema(description = "Generert G-verdi for året")
        private Float generatedG;

        @Schema(description = "Grunnbeløp for året")
        private Boolean grunnbelop;
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
    public static class Pensjonsavtale {
        public enum AvtaleKategori {
            NONE, UNKNOWN, INDIVIDUELL_ORDNING, PRIVAT_AFP,
            PRIVAT_TJENESTEPENSJON, OFFENTLIG_TJENESTEPENSJON, FOLKETRYGD
        }

        private String produktBetegnelse;
        private AvtaleKategori avtaleKategori;
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
            private Integer startAlderMaaned;
            private Integer sluttAlderAar;
            private Integer sluttAlderMaaned;
            private Integer aarligUtbetaling;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Alderspensjon {

        public enum AfpPrivatResultat {
            INNVILGET,
            AVSLATT,
            TRUKKET,
            VENTER_PAA_FELLESORDNINGEN
        }

        private LocalDate iverksettelsesdato;

        private LocalDate kravFremsattDato;

        private String saksbehandler;
        private String attesterer;
        private String navEnhetId;

        @Schema
        private Integer uttaksgrad;

        private Boolean soknad;

        private Boolean inkluderAfpPrivat;
        private AfpPrivatResultat afpPrivatResultat;

        @Schema
        private List<SkjemaRelasjon> relasjoner;

        public List<SkjemaRelasjon> getRelasjoner() {

            if (isNull(relasjoner)) {
                relasjoner = new ArrayList<>();
            }
            return relasjoner;
        }

        public boolean isSoknad() {

            return isTrue(soknad);
        }

        @JsonIgnore
        public boolean isVedtak() {

            return isNotTrue(soknad);
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
        private Integer inntektEtterUforhet;
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AfpOffentlig {

        @Schema(description = "Liste av tpId som støttes direkte for denne personen, " +
                "mulige verdier hentes her: /api/mock-oppsett/muligedirektekall")
        private List<String> direktekall;

        @Schema(description = "AFP offentlig som denne personen har (stub)")
        private List<AfpOffentligStub> mocksvar;

        public List<String> getDirektekall() {

            if (isNull(direktekall)) {
                direktekall = new ArrayList<>();
            }
            return direktekall;
        }

        public List<AfpOffentligStub> getMocksvar() {

            if (isNull(mocksvar)) {
                mocksvar = new ArrayList<>();
            }
            return mocksvar;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AfpOffentligStub {

        @Schema(description = "TpId som skal stubbes, liste kan hentes her /api/v1/tp/ordning")
        private String tpId;

        private StatusAfp statusAfp;
        private LocalDate virkningsDato;
        @Min(2024)
        @Schema(description = "Årstall (fra dropdown?), laveste verdi er 2024")
        private Integer sistBenyttetG;
        private List<DatoBeloep> belopsListe;

        public List<DatoBeloep> getBelopsListe() {

            if (isNull(belopsListe)) {
                belopsListe = new ArrayList<>();
            }
            return belopsListe;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DatoBeloep {

        private LocalDate fomDato;
        @Min(1)
        @Max(2147483647)
        private Integer belop;
    }

    public enum StatusAfp {UKJENT, INNVILGET, SOKT, AVSLAG, IKKE_SOKT}

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlderspensjonNyUtaksgrad {

        private Integer nyUttaksgrad;
        private LocalDate fom;
        private String saksbehandler;
        private String attesterer;
        private String navEnhetId;
    }
}
