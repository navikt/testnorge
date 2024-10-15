package no.nav.testnav.libs.dto.oppdragservice.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OppdragRequest {

    @Size(min = 1, max = 4)
    private List<String> bilagstype;
    private List<Avstemmingsnokkel> avstemmingsnokkel;
    private Ompostering ompostering;
    private List<Oppdragslinje> oppdragslinje;
    @NotBlank
    private String kodeEndring;
    private KodeStatus kodeStatus;
    private String datoStatusFom;
    @NotBlank
    private String kodeFagomraade;
    private String fagsystemId;
    private Long oppdragsId;
    private String utbetFrekvens;
    private String datoForfall;
    private String stonadId;
    @NotBlank
    @Size(min = 9, max = 11)
    @Schema(description = "Fødselsnummer eller organisasjonsnummer oppdraget gjelder for")
    private String oppdragGjelderId;
    @NotBlank
    private String datoOppdragGjelderFom;
    @NotBlank
    private String saksbehId;
    private List<Enhet> enhet;
    private List<Belopsgrense> belopsgrense;
    private List<Tekst> tekst;

    public List<String> getBilagstype() {

        if (isNull(bilagstype)) {
            bilagstype = new ArrayList<>();
        }
        return bilagstype;
    }

    public List<Avstemmingsnokkel> getAvstemmingsnokkel() {

        if (isNull(avstemmingsnokkel)) {
            avstemmingsnokkel = new ArrayList<>();
        }
        return avstemmingsnokkel;
    }

    public List<Oppdragslinje> getOppdragslinje() {

        if (isNull(oppdragslinje)) {
            oppdragslinje = new ArrayList<>();
        }
        return oppdragslinje;
    }

    public List<Enhet> getEnhet() {

        if (isNull(enhet)) {
            enhet = new ArrayList<>();
        }
        return enhet;
    }

    public List<Belopsgrense> getBelopsgrense() {

        if (isNull(belopsgrense)) {
            belopsgrense = new ArrayList<>();
        }
        return belopsgrense;
    }

    public List<Tekst> getTekst() {

        if (isNull(tekst)) {
            tekst = new ArrayList<>();
        }
        return tekst;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Avstemmingsnokkel {

        @NotBlank
        private String kodeKomponent;
        @NotBlank
        private String avstemmingsNokkel;
        @NotBlank
        private String tidspktReg;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ompostering {

        @NotBlank
        private String omPostering;
        private String datoOmposterFom;
        private String feilreg;
        @NotBlank
        private String tidspktReg;
        @NotBlank
        private String saksbehId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Oppdragslinje {

        private RefusjonsInfo refusjonsInfo;
        private List<Tekst> tekst;
        @Schema(description = "Fra XSD: Referanse ID 120 dersom enhet på nivå oppdrag og " +
                "Referanse ID 160 dersom enhet på nivå oppdragslinje")
        private List<Enhet> enhet;
        private List<Grad> grad;
        private List<Attestant> attestant;
        private List<Valuta> valuta;
        @NotBlank
        private String kodeEndringLinje;
        private KodeStatusLinje kodeStatusLinje;
        private LocalDate datoStatusFom;
        private String vedtakId;
        private String delytelseId;
        private BigInteger linjeId;
        @NotBlank
        private String kodeKlassifik;
        private String datoKlassifikFom;
        @NotBlank
        private LocalDate datoVedtakFom;
        private LocalDate datoVedtakTom;
        @NotBlank
        private BigDecimal sats;
        @NotBlank
        private FradragTillegg fradragTillegg;
        @NotBlank
        private String typeSats;
        private String skyldnerId;
        private LocalDate datoSkyldnerFom;
        private String kravhaverId;
        private LocalDate datoKravhaverFom;
        private String kid;
        private LocalDate datoKidFom;
        private String brukKjoreplan;
        @NotBlank
        private String saksbehId;
        @NotBlank
        private String utbetalesTilId;
        private LocalDate datoUtbetalesTilIdFom;
        @NotBlank
        private KodeArbeidsgiver kodeArbeidsgiver;
        private String henvisning;
        private String typeSoknad;
        private String refFagsystemId;
        private Long refOppdragsId;
        private String refDelytelseId;
        private BigInteger refLinjeId;

        public List<Tekst> getTekst() {

            if (isNull(tekst)) {
                tekst = new ArrayList<>();
            }
            return tekst;
        }

        public List<Enhet> getEnhet() {

            if (isNull(enhet)) {
                enhet = new ArrayList<>();
            }
            return enhet;
        }

        public List<Grad> getGrad() {

            if (isNull(grad)) {
                grad = new ArrayList<>();
            }
            return grad;
        }

        public List<Attestant> getAttestant() {

            if (isNull(attestant)) {
                attestant = new ArrayList<>();
            }
            return attestant;
        }

        public List<Valuta> getValuta() {

            if (isNull(valuta)) {
                valuta = new ArrayList<>();
            }
            return valuta;
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefusjonsInfo {

        private String refunderesId;
        private LocalDate maksDato;
        private LocalDate datoFom;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tekst {

        private BigInteger tekstLnr;
        private String tekstKode;
        private String tekst;
        @NotBlank
        private LocalDate datoTekstFom;
        private LocalDate datoTekstTom;
        private String feilreg;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Enhet {

        @NotBlank
        @Size(min = 1, max = 4)
        private String typeEnhet;
        @Size(min = 4, max = 13)
        @Schema(description = "Enhet er tknr evt orgnr + avdeling")
        private String enhet;
        @NotBlank
        private LocalDate datoEnhetFom;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Grad {

        @NotBlank
        protected String typeGrad;
        @NotBlank
        protected BigInteger grad;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Attestant {

        @NotBlank
        protected String attestantId;
        protected LocalDate datoUgyldigFom;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Valuta {

        @NotBlank
        protected String typeValuta;
        @NotBlank
        protected String valuta;
        @NotBlank
        protected LocalDate datoValutaFom;
        protected String feilreg;
    }

    public enum KodeStatusLinje {
        OPPH,
        HVIL,
        SPER,
        REAK;
    }

    public enum FradragTillegg {
        F,
        T;
    }

    public enum KodeArbeidsgiver {
        A,
        S,
        P;
    }

    public enum KodeStatus {
        NY,
        LOPE,
        HVIL,
        SPER,
        IKAT,
        ATTE,
        ANNU,
        OPPH,
        FBER,
        REAK,
        KORR,
        FEIL;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Belopsgrense {

        @NotBlank
        private String typeGrense;
        @NotBlank
        private BigDecimal belopGrense;
        @NotBlank
        private LocalDate datoGrenseFom;
        private LocalDate datoGrenseTom;
        private String feilreg;
    }
}
