package no.nav.registre.syntrest.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PenResponse {
    //    [
//  {
//    "FLYKTNING": 0,
//    "GRUNNLAG_LAST": 0,
//    "GYLDIG_VILKAR_PROV": 0,
//    "ID": 0,
//    "K_INITIERT_AV": "BRUKER",
//    "K_KANAL_BPROF_T": "",
//    "K_KOMMSJN_FORM": "ELEK",
//    "K_KRAV_GJELDER": "F_BH_MED_UTL",
//    "OMSORG_BARN_UNDER_7_AR": 0,
//    "SOKT_AFP_PRIVAT": 1,
//    "STATSBORGER_I_LAND": 8,
//    "UTTAKSGRAD": 0,
//    "UTVANDRET": 0,
//    "bostedsland": 10,
//    "dato_sivilstand_fom": "29.05.1999",
//    "dato_virk_onsket": "01.03.2011",
//    "har_samboer": 0,
//    "k_sivilstatus_t": "GIFT"
//  }
//]
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
