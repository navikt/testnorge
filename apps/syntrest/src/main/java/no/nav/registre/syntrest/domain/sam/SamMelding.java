package no.nav.registre.syntrest.domain.sam;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SamMelding {

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
    private String art;
    @JsonProperty("K_FAGOMRADE")
    private String fagomraade;
    @JsonProperty("K_KANAL_T")
    private String kanal;
    @JsonProperty("K_MELDING_STATUS")
    private String meldingStatus;
    @JsonProperty("K_SAM_HENDELSE_T")
    private String samHendelse;
    @JsonProperty("K_TP_ART")
    private String tpArt;
    @JsonProperty("K_VEDTAK_STATUS")
    private String vedtakStatus;
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
