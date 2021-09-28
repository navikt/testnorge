package no.nav.registre.sam.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyntetisertSamordningsmelding {

    @JsonProperty("ANTALL_FORSOK")
    private String antallForsoek;
    @JsonProperty("DATO_ENDRET")
    private String datoEndret;
    @JsonProperty("DATO_FOM")
    private String datoFom;
    @JsonProperty("DATO_OPPRETTET")
    private String datoOpprettet;
    @JsonProperty("DATO_PURRET")
    private String datoPurret;
    @JsonProperty("DATO_SENDT")
    private String datoSendt;
    @JsonProperty("DATO_SVART")
    private String datoSvart;
    @JsonProperty("DATO_TOM")
    private String datoTom;
    @JsonProperty("ENDRET_AV")
    private String datoEndretAv;
    @JsonProperty("ETTERBETALING")
    private String etterbetaling;
    @JsonProperty("K_ART")
    private String kArt;
    @JsonProperty("K_FAGOMRADE")
    private String kFagomraade;
    @JsonProperty("K_KANAL_T")
    private String kKanalT;
    @JsonProperty("K_MELDING_STATUS")
    private String kMeldingStatus;
    @JsonProperty("K_SAM_HENDELSE_T")
    private String kSamHendelseT;
    @JsonProperty("K_TP_ART")
    private String kTPArt;
    @JsonProperty("K_VEDTAK_STATUS")
    private String kVedtakStatus;
    @JsonProperty("OPPRETTET_AV")
    private String opprettetAv;
    @JsonProperty("PURRING")
    private String purring;
    @JsonProperty("REFUSJONSKRAV")
    private String refusjonskrav;
    @JsonProperty("SAK_ID_FK")
    private String sakIdFk;
    @JsonProperty("TSS_EKSTERN_ID_FK")
    private String tssEksternIdFk;
    @JsonProperty("VERSJON")
    private String versjon;
}