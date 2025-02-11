package no.nav.dolly.bestilling.arbeidssoekerregisteret.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArbeidssoekerregisteretRequest {

    private String identitetsnummer;
    private String utfoertAv; // Enum: [ UKJENT_VERDI, UDEFINERT, VEILEDER, SYSTEM, SLUTTBRUKER ]
    private String kilde;
    private String aarsak;
    private String nuskode;
    private Boolean utdanningBestaatt;
    private Boolean utdanningGodkjent;
    private String jobbsituasjonsbeskrivelse; //Enum: [ UKJENT_VERDI, UDEFINERT, HAR_SAGT_OPP, HAR_BLITT_SAGT_OPP, ER_PERMITTERT, ALDRI_HATT_JOBB, IKKE_VAERT_I_JOBB_SISTE_2_AAR, AKKURAT_FULLFORT_UTDANNING, VIL_BYTTE_JOBB, USIKKER_JOBBSITUASJON, MIDLERTIDIG_JOBB, DELTIDSJOBB_VIL_MER, NY_JOBB, KONKURS, ANNET ]
    private JobbsituasjonDetaljer jobbsituasjonsdetaljer;

    private Boolean helsetilstandHindrerArbeid;
    private Boolean andreForholdHindrerArbeid;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class JobbsituasjonDetaljer {

        private LocalDate gjelderFraDato;
        private LocalDate gjelderTilDato;
        private Integer stillingStyrk08; // Stillingskode i STYRK08 fra kodeverk "Yrkesklassifisering"
        private String stillingstittel; // Stillingstittel beskrivelse i STYRK08 fra kodeverk "Yrkesklassifisering"
        private Integer stillingsprosent;
        private LocalDate sisteDagMedLoenn;
        private LocalDate sisteArbeidsdag;
    }
}
