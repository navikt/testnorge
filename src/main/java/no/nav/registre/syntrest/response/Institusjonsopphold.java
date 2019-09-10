package no.nav.registre.syntrest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Institusjonsopphold {
    @JsonProperty("dato_fom")
    private String startdato;
    @JsonProperty("dato_tom")
    private String faktiskSluttdato;
    @JsonProperty("dato_tom_forventet")
    private String forventetSluttdato;
    @JsonProperty("k_kilde_inst_t")
    private String kilde;
    @JsonProperty("k_opphold_inst_t")
    private String instutusjonstype;
    @JsonProperty("k_pas_ka_inst_t")
    private String kategori;
    @JsonProperty("k_varig_inst_t")
    private String varighet;
    @JsonProperty("overfort")
    private int overfoert;
    @JsonProperty("tss_ekstern_id_fk")
    private String tssEksternId;
}
