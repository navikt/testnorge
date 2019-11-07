package no.nav.registre.syntrest.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PenResponse {

    @JsonProperty("FLYKTNING")
    private int flyktning;

    @JsonProperty("GRUNNLAG_LAST")
    private int grunnlagLast;

    @JsonProperty("GYLDIG_VILKAR_PROV")
    private int gyldigVilkaarProv;

    @JsonProperty("ID")
    private int id;

    @JsonProperty("K_INITIERT_AV")
    private String initiertAv;

    @JsonProperty("K_KANAL_BPROF_T")
    private String kanal;

    @JsonProperty("K_KOMMSJN_FORM")
    private String kommsjnForm;

    @JsonProperty("K_KRAV_GJELDER")
    private String kravGjelder;

    @JsonProperty("OMSORG_BARN_UNDER_7_AR")
    private String omsorgBarnUnderSyvAar;

    @JsonProperty("SOKT_AFP_PRIVAT")
    private int soektAFPprivat;

    @JsonProperty("STATSBORGER_I_LAND")
    private int statsborgerILand;

    @JsonProperty("UTTAKSGRAD")
    private int uttaksgrad;

    @JsonProperty("UTVANDRET")
    private int utvandret;

    @JsonProperty("bostedsland")
    private int bostedsland;

    @JsonProperty("dato_sivilstand_fom")
    private String datoSivilstandFom;

    @JsonProperty("dato_virk_onsket")
    private String datoVirkOensket;

    @JsonProperty("har_samboer")
    private int harSamboer;

    @JsonProperty("k_sivilstatus_t")
    private String sivilstatus;
}
