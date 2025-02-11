package no.nav.dolly.domain.resultset.arbeidssoekerregistrering;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsArbeidssoekerregisteret {

    @Schema(description = "Brukertype (enum) som har utført registreringen")
    private String utfoertAv; // Enum: [ UKJENT_VERDI, UDEFINERT, VEILEDER, SYSTEM, SLUTTBRUKER ]
    @Schema(example = "Dolly")
    private String kilde;
    @Schema(example = "Registrerer testbruker i dev")
    private String aarsak;
    @Schema(description = "Nuskode (enum) som beskriver nivået på utdanningen")
    private String nuskode;
    @Schema(description = "Angir om utdanningen er bestått for Nuskode 3 og høyere")
    private Boolean utdanningBestaatt;
    @Schema(description = "Angir om utdanningen er godkjent for Nuskode 3 og høyere")
    private Boolean utdanningGodkjent;
    @Schema(description = "Beskrivelse av jobbsituasjonen (enum)")
    private String jobbsituasjonsbeskrivelse; //Enum: [ UKJENT_VERDI, UDEFINERT, HAR_SAGT_OPP, HAR_BLITT_SAGT_OPP, ER_PERMITTERT, ALDRI_HATT_JOBB, IKKE_VAERT_I_JOBB_SISTE_2_AAR, AKKURAT_FULLFORT_UTDANNING, VIL_BYTTE_JOBB, USIKKER_JOBBSITUASJON, MIDLERTIDIG_JOBB, DELTIDSJOBB_VIL_MER, NY_JOBB, KONKURS, ANNET ]

    @Schema(description = "Detaljer om jobbsituasjonen")
    private JobbsituasjonDetaljer jobbsituasjonsdetaljer;

    @Schema(description = "Angir om helsetilstanden hindrer arbeid")
    private Boolean helsetilstandHindrerArbeid;
    @Schema(description = "Angir om andre forhold hindrer arbeid")
    private Boolean andreForholdHindrerArbeid;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobbsituasjonDetaljer {
        @Schema(description = "Dato fra når jobbsituasjonen gjelder")
        private LocalDate gjelderFraDato;
        @Schema(description = "Dato til når jobbsituasjonen gjelder")
        private LocalDate gjelderTilDato;
        @Schema(description = "Stillingskode i STYRK08 fra kodeverk \"Yrkesklassifisering\"")
        private Integer stillingStyrk08;
        @Schema(description = "Stillingstittel beskrivelse i STYRK08 fra kodeverk \"Yrkesklassifisering\"")
        private String stillingstittel;
        @Schema(description = "Stillingsprosent")
        private Integer stillingsprosent;
        @Schema(description = "Dato for siste dag med lønn")
        private LocalDate sisteDagMedLoenn;
        @Schema(description = "Dato for siste arbeidsdag")
        private LocalDate sisteArbeidsdag;
    }
}