package no.nav.dolly.synt.aap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AapVedtakDto {

    @JsonProperty("AAR")
    private String aar;

    @JsonProperty("AKTFASEKODE")
    private String aktfasekode;

    @JsonProperty("ANDRE_OKONOM_YTELSER")
    private List<Map<String, List<YtelseDto>>> andreOkonomYtelser;

    @JsonProperty("AVBRUDDSKODE")
    private String avbruddskode;

    @JsonProperty("DATO_MOTTATT")
    private String datoMottatt;

    @JsonProperty("FRA_DATO")
    private String fraDato;

    @JsonProperty("GEN_SAKSOPPLYSNINGER")
    private List<OpplysningDto> genSaksopplysninger;

    @JsonProperty("INSTITUSJONSOPPHOLD")
    private List<OpplysningDto> institusjonsopphold;

    @JsonProperty("JUSTERT_FRA")
    private String justertFra;

    @JsonProperty("LOPENRVEDTAK")
    private String lopenrvedtak;

    @JsonProperty("MEDLEM_FOLKETRYGDEN")
    private List<YtelseDto> medlemFolketrygden;

    @JsonProperty("NYTTKRAV2")
    private String nyttkrav2;

    @JsonProperty("OPPR_TIL_DATO")
    private String opprTilDato;

    @JsonProperty("PERIODE")
    private List<YtelseDto> periode;

    @JsonProperty("TIL_DATO")
    private String tilDato;

    @JsonProperty("UTFALL")
    private String utfall;

    @JsonProperty("VEDTAKSVARIANT")
    private String vedtaksvariant;

    @JsonProperty("VEDTAKTYPE")
    private String vedtaktype;

    @JsonProperty("VEDTAK_DATO")
    private String vedtakDato;

    @JsonProperty("VILKAAR")
    private List<VilkaarDto> vilkaar;
}

