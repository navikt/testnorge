package no.nav.registre.syntrest.domain.medl;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Medlemskapsmelding {

    @JsonProperty("dato_registrert")
    private String datoRegistrert;

    @JsonProperty("periode_tom")
    private String periodeTom;

    @JsonProperty("type")
    private String type;

    @JsonProperty("statsborgerland")
    private String statsborgerland;

    @JsonProperty("endret_av")
    private String endretAv;

    @JsonProperty("statusaarsak")
    private String statusaarsak;

    @JsonProperty("delstudier")
    private String delstudier;

    @JsonProperty("godkjent")
    private String godkjent;

    @JsonProperty("dato_endret")
    private String datoEndret;

    @JsonProperty("land")
    private String land;

    @JsonProperty("periode_fom")
    private String periodeFom;

    @JsonProperty("kilde")
    private String kilde;

    @JsonProperty("inndato_laanekassen")
    private String inndatoLaanekassen;

    @JsonProperty("dato_identifisering")
    private String datoIdentifisering;

    @JsonProperty("dekning")
    private String dekning;

    @JsonProperty("grunnlag")
    private String grunnlag;

    @JsonProperty("dato_opprettet")
    private String datoOpprettet;

    @JsonProperty("dato_bruk_til")
    private String datoBrukTil;

    @JsonProperty("soknad")
    private String soknad;

    @JsonProperty("versjon")
    private String versjon;

    @JsonProperty("kildedokument")
    private String kildedokument;

    @JsonProperty("dato_bruk_fra")
    private String datoBrukFra;

    @JsonProperty("studieland")
    private String studieland;

    @JsonProperty("dato_beslutning")
    private String datoBeslutning;

    @JsonProperty("opprettet_av")
    private String opprettetAv;

    @JsonProperty("lovvalg")
    private String lovvalg;

    @JsonProperty("status")
    private String status;

}
