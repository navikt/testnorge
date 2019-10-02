package no.nav.registre.arena.core.consumer.rs.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AAPMelding {

    // OKONOM YTELSE - koder:
    @JsonProperty
    String TYPE;
    @JsonProperty
    String FDATO;
    @JsonProperty
    String TDATO;
    @JsonProperty
    String GRAD;
    @JsonProperty
    String BELOP;
    @JsonProperty
    String BELPR;

    // // GEN SAKSOPPLYSNINGER
    // koder: overordnet: OOPL TLONN
    @JsonProperty
    String KDATO;
    @JsonProperty
    String BTID;
    @JsonProperty
    String TUUIN;
    @JsonProperty
    String UUFOR;
    @JsonProperty
    String STUBE;
    @JsonProperty
    String OTILF;
    @JsonProperty
    String OTSEK;
    @JsonProperty
    String OOPPL; // overordnet
    @JsonProperty
    String BDSAT; // ooppl
    @JsonProperty
    String UFTID; // ooppl
    @JsonProperty
    String BDSRP; // ooppl
    @JsonProperty
    String GRDRP; // ooppl
    @JsonProperty
    String GRDTU; // ooppl
    @JsonProperty
    String BDSTU; // ooppl
    @JsonProperty
    String GGRAD; // ooppl
    @JsonProperty
    String ORIGG; // ooppl
    @JsonProperty
    String YDATO;
    @JsonProperty
    String YINNT;
    @JsonProperty
    String YGRAD;
    @JsonProperty
    String TLONN; // overordnet
    @JsonProperty
    String SPROS; // tlonn
    @JsonProperty
    String NORIN; // tlonn
    @JsonProperty
    String FAKIN; // tlonn
    @JsonProperty
    String EOS;
    @JsonProperty
    String LAND;

    // // Insitiusjonsopphold // overordnet: inper
    @JsonProperty
    String STRFG;
    @JsonProperty
    String INSTA;
    @JsonProperty
    String FRIKL;
    @JsonProperty
    String INPER;
    @JsonProperty
    String INFRA;
    @JsonProperty
    String INTIL;
    @JsonProperty
    String REDPR;
    @JsonProperty
    String INSUD;

    // Vilkaar
    @JsonProperty("18-67AAR2")
    String attensekstisyvaar2;
    @JsonProperty
    String MEDLSKAP12;
    @JsonProperty
    String AOINORGE2;
    @JsonProperty
    String INTKTBFAL2;
    @JsonProperty
    String A11_52;
    @JsonProperty
    String YTELARBGI2;
    @JsonProperty
    String IVLGANNEN2;
    @JsonProperty
    String AIANNEN2;
    @JsonProperty
    String STRAFFGJF2;
    @JsonProperty
    String AAFAARBIS2;
    @JsonProperty
    String ABIDRARAK2;
    @JsonProperty
    String AAIUBEGRF2;
    @JsonProperty
    String AAMEDVPLA2;
    @JsonProperty
    String AABEHGSTUD;
    @JsonProperty
    String AAANNENSYK;
    @JsonProperty
    String AAFERD2;
    @JsonProperty
    String AAFOPTILUO;
    @JsonProperty
    String AASAMMESYK;
    @JsonProperty
    String AASYKFORUO;
    @JsonProperty
    String AASYKTILUO;
    @JsonProperty
    String UVUT2;
    @JsonProperty
    String AALANGUTRE;
    @JsonProperty
    String AAOPPLTILT;
    @JsonProperty
    String AASYKDOSKA;
    @JsonProperty
    String AASYKSKADE;


    // Ytterst i AAP
    @JsonProperty
    String AKTFASEKODE; // aktivitetsfase
    @JsonProperty
    String DATO_MOTTATT; // datoMottatt
    @JsonProperty
    String FRA_DATO; // fraDato
    @JsonProperty
    String TIL_DATO; // tilDato
    @JsonProperty
    String UTFALL; // utfall
    @JsonProperty
    String VEDTAKSVARIANT; // vedtaksvariant
}