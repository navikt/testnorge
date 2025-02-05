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
public class ArbeidssokerregisteretRequest {

    private String identitetsnummer;
    private String utfoertAv; // Enum: [ UKJENT_VERDI, UDEFINERT, VEILEDER, SYSTEM, SLUTTBRUKER ]
    private String kilde;
    private String example;
    private String aarsak;
    private String nuskode;
    private Boolean utdanningBestaatt;
    private Boolean utdanningGodkjent;
    private String jobbsituasjonBeskrivelse; //Enum: [ UKJENT_VERDI, UDEFINERT, HAR_SAGT_OPP, HAR_BLITT_SAGT_OPP, ER_PERMITTERT, ALDRI_HATT_JOBB, IKKE_VAERT_I_JOBB_SISTE_2_AAR, AKKURAT_FULLFORT_UTDANNING, VIL_BYTTE_JOBB, USIKKER_JOBBSITUASJON, MIDLERTIDIG_JOBB, DELTIDSJOBB_VIL_MER, NY_JOBB, KONKURS, ANNET ]
    private JobbsituasjonDetaljer jobbsituasjonDetaljer;

    private Boolean helsetilstandHindrerArbeid;
    private Boolean andreForholdHindrerArbeid;
    
    public static class JobbsituasjonDetaljer {

        private String gjelderFraDatoIso8601;
        private String gjelderTilDatoIso8601;
        private String stillingStyrk08;
        private String stilling;
        private Integer prosent;
        private LocalDate sisteDagMedLoennIso8601;
        private LocalDate sisteArbeidsdagIso8601;
    }
}
