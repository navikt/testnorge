package no.nav.registre.syntrest.domain.tp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TPmelding {

    @JsonProperty("dato_innm_ytel_fom")
    private java.sql.Date datoInnmeldt;

    @JsonProperty("k_ytelse_t")
    private String ytelse;

    @JsonProperty("k_melding_t")
    private String melding;

    @JsonProperty("dato_ytel_iver_fom")
    private java.sql.Date datoIverksattFom;

    @JsonProperty("dato_ytel_iver_tom")
    private java.sql.Date datoIverksattTom;

    @JsonProperty("dato_opprettet")
    private java.sql.Timestamp datoOpprettet;

    @JsonProperty("opprettet_av")
    private String opprettetAv;

    @JsonProperty("dato_endret")
    private java.sql.Timestamp datoEndret;

    @JsonProperty("endret_av")
    private String endretAv;

    @JsonProperty("versjon")
    private String versjon;

    @JsonProperty("er_gyldig")
    private String erGyldig;

    @JsonProperty("funk_ytelse_id")
    private String ytelseId;

    @JsonProperty("dato_bruk_fom")
    private java.sql.Timestamp datoBrukFom;

    @JsonProperty("dato_bruk_tom")
    private java.sql.Timestamp datoBrukTom;
}
