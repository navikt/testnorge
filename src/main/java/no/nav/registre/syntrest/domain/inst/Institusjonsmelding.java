package no.nav.registre.syntrest.domain.inst;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Institusjonsmelding {

    @JsonProperty("tss_ekstern_id_fk")
    private Long tssEksternId;

    @JsonProperty("k_opphold_inst_t")
    private String institusjonstype;

    @JsonProperty("k_pas_ka_inst_t")
    private String kategori;

    @JsonProperty("dato_fom")
    private String startdato;

    @JsonProperty("dato_tom")
    private String faktiskSluttdato;

    @JsonProperty("dato_tom_forventet")
    private String forventetSluttdato;

    @JsonProperty("k_kilde_inst_t")
    private String kilde;

    @JsonProperty("k_varig_inst_t")
    private String varighet;

    @JsonProperty("overfort")
    private boolean overfoert;

}