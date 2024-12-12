package no.nav.testnav.libs.dto.oppdragservice.v1;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Oppdrag {

    private List<Bilagstype> bilagstype;
    private List<Avstemmingsnokkel> avstemmingsnokkel;
    private Ompostering ompostering;
    private List<Oppdragslinje> oppdragslinje;

    @NotBlank
    private KodeEndring kodeEndring;
    private KodeStatus kodeStatus;
    private LocalDate datoStatusFom;
    @NotBlank
    @Schema(minLength = 1, maxLength = 8)
    private String kodeFagomraade;
    @Schema(maxLength = 30)
    private String fagsystemId;
    @Schema(maxLength = 10)
    private Long oppdragsId;
    private UtbetalingFrekvensType utbetFrekvens;
    private LocalDate datoForfall;
    @Schema(maxLength = 10)
    private String stonadId;
    @NotBlank
    @Schema(description = "Angir fødselsnummer eller organisasjonsnummer oppdraget gjelder for", minLength = 9, maxLength = 11)
    private String oppdragGjelderId;
    @NotBlank
    private LocalDate datoOppdragGjelderFom;
    @NotBlank
    @Schema(maxLength = 8)
    private String saksbehId;

    private List<Enhet> enhet;
    private List<Belopsgrense> belopsgrense;
    private List<Tekst> tekst;

    public List<Bilagstype> getBilagstype() {

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
    @Schema(description = "Bilagstype, Referanse ID 113")
    public static class Bilagstype {

        @Schema(description = "Kode for type av bilag", minLength = 1, maxLength = 2)
        private String typeBilag;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Entitet nøkler til bevis informasjon, Referanse ID 115")
    public static class Avstemmingsnokkel {

        @NotBlank
        @Schema(minLength = 1, maxLength = 8)
        private String kodeKomponent;
        @NotBlank
        @Schema(minLength = 1, maxLength = 8)
        private String avstemmingsNokkel;
        @NotBlank
        private LocalDateTime tidspktReg;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Ompostering, Referanse ID 116")
    public static class Ompostering {

        @NotBlank
        private JaNei omPostering;
        private LocalDate datoOmposterFom;
        @Schema(description = "Kode for beskrivelse av feil", minLength = 0, maxLength = 1)
        private String feilreg;
        @NotBlank
        private LocalDateTime tidspktReg;
        @NotBlank
        @Schema(maxLength = 8)
        private String saksbehId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Oppdragslinje, Referanse ID 115")
    public static class Oppdragslinje {

        @NotBlank
        private KodeEndringType kodeEndringLinje;
        private KodeStatusLinje kodeStatusLinje;
        private LocalDate datoStatusFom;
        @Schema(maxLength = 10)
        private String vedtakId;
        @Schema(maxLength = 30)
        private String delytelseId;
        @Schema(maxLength = 5)
        private Integer linjeId;
        @NotBlank
        @Schema(minLength = 1, maxLength = 50)
        private String kodeKlassifik;
        private LocalDate datoKlassifikFom;
        @NotBlank
        private LocalDate datoVedtakFom;
        private LocalDate datoVedtakTom;
        @NotBlank
        @Schema(description = "maximal toal lengde = 13, antall desimaler = 2")
        private BigDecimal sats;
        @NotBlank
        private FradragTillegg fradragTillegg;
        @NotBlank
        private SatsType typeSats;
        @Schema(description = "Angir fødselsnummer eller organisasjonsnummer på skyldneren", minLength = 9, maxLength = 11)
        private String skyldnerId;
        private LocalDate datoSkyldnerFom;
        @Schema(description = "Angir fødselsnummer eller organisasjonsnummer på kravhaver", minLength = 9, maxLength = 11)
        private String kravhaverId;
        private LocalDate datoKravhaverFom;
        @Schema(maxLength = 26)
        private String kid;
        private LocalDate datoKidFom;
        @Schema(maxLength = 1)
        private String brukKjoreplan;
        @NotBlank
        @Schema(maxLength = 8)
        private String saksbehId;
        @NotBlank
        @Schema(description = "Angir fødselsnummer eller organisasjonsnummer på kravhaver", minLength = 9, maxLength = 11)
        private String utbetalesTilId;
        private LocalDate datoUtbetalesTilIdFom;
        @NotBlank
        private KodeArbeidsgiver kodeArbeidsgiver;
        @Schema(maxLength = 30)
        private String henvisning;
        @Schema(maxLength = 10)
        private String typeSoknad;
        @Schema(maxLength = 30)
        private String refFagsystemId;
        @Schema(maxLength = 10)
        private Long refOppdragsId;
        @Schema(maxLength = 30)
        private String refDelytelseId;
        @Schema(maxLength = 5)
        private Integer refLinjeId;

        private RefusjonsInfo refusjonsInfo;
        private List<Tekst> tekst;

        private List<Enhet> enhet;
        private List<Grad> grad;
        private List<Attestant> attestant;
        private List<Valuta> valuta;

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
    @Schema(description = "Entitet for refusjon til et orgnr og/eller maksdato for utbetaling av ytelse, Referanse ID 156")
    public static class RefusjonsInfo {

        @Schema(description = "Angir fødselsnummer eller organisasjonsnummer på kravhaver", minLength = 9, maxLength = 11)
        private String refunderesId;
        private LocalDate maksDato;
        private LocalDate datoFom;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Entitet Tekst, Referanse ID 140 dersom tekst er tilhørende oppdrag, " +
            "Referanse ID 158 dersom tekst er tilhørende oppdragslinje")
    public static class Tekst {

        @NotBlank
        @Schema(minLength = 1, maxLength = 2)
        private Integer tekstLnr;
        @Schema(maxLength = 4)
        private String tekstKode;
        @Schema
        private String tekst;
        @NotBlank
        private LocalDate datoTekstFom;
        private LocalDate datoTekstTom;
        @Schema(description = "Kode for beskrivelse av feil", minLength = 0, maxLength = 1)
        private String feilreg;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Entitet enhet, Referanse ID 120 dersom enhet er på nivå oppdrag, " +
            "Referanse ID 160 dersom enhet er på nivå oppdragslinje")
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
    @Schema(description = "Entitet Grad, Referanse ID 170")
    public static class Grad {

        @NotBlank
        @Schema(minLength = 1, maxLength = 4)
        protected String typeGrad;
        @NotBlank
        @Schema(description = "Prosentgrad, maks 100")
        protected Integer grad;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Entitet Attestasjon, Referanse ID 180")
    public static class Attestant {

        @NotBlank
        @Schema(minLength = 1, maxLength = 8)
        protected String attestantId;
        protected LocalDate datoUgyldigFom;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Entitet Valuta, Referanse ID 190")
    public static class Valuta {

        @NotBlank
        protected Oppdrag.ValutaType typeValuta;
        @NotBlank
        @Schema(minLength = 1, maxLength = 3)
        protected String valuta;
        @NotBlank
        protected LocalDate datoValutaFom;
        @Schema(description = "Kode for beskrivelse av feil", minLength = 0, maxLength = 1)
        protected String feilreg;
    }

    public enum JaNei {
        J,
        N
    }

    public enum KodeStatusLinje {
        OPPH,
        HVIL,
        SPER,
        REAK
    }

    public enum FradragTillegg {
        F,
        T
    }

    public enum KodeArbeidsgiver {
        A,
        S,
        P
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
        FEIL
    }

    public enum KodeEndring {
        NY,
        ENDR,
        UEND
    }

    public enum KodeEndringType {
        NY,
        ENDR
    }

    public enum SatsType {

        DAG,
        UKE,
        _14DB,
        MND,
        AAR,
        ENG,
        AKTO
    }

    public enum UtbetalingFrekvensType {

        DAG,
        UKE,
        MND,
        _14DG,
        ENG
    }

    public enum ValutaType {

        FAKT,
        FRAM,
        UTB
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Entitet Beløpsgrense, Referanse ID 130")
    public static class Belopsgrense {

        @NotBlank
        @Schema(minLength = 1, maxLength = 4)
        private String typeGrense;
        @NotBlank
        @Schema(description = "Typen beskriver formatet og begrensningene til beløp, maks totalt antall sifre=11, desimaler=2")
        private BigDecimal belopGrense;
        @NotBlank
        private LocalDate datoGrenseFom;
        private LocalDate datoGrenseTom;
        @Schema(description = "Kode for beskrivelse av feil", minLength = 0, maxLength = 1)
        private String feilreg;
    }
}
