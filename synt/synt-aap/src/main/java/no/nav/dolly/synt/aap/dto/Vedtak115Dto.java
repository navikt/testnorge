package no.nav.dolly.synt.aap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vedtak115Dto {

    @JsonProperty("DATO_MOTTATT")
    private String datoMottatt;

    @JsonProperty("FRA_DATO")
    private String fraDato;

    @JsonProperty("MEDISINSK_OPPLYSNING")
    private List<MedisinskOpplysningDto> medisinskOpplysning;

    @JsonProperty("TIL_DATO")
    private String tilDato;

    @JsonProperty("UTFALL")
    private String utfall;

    @JsonProperty("VEDTAKTYPE")
    private String vedtaktype;

    @JsonProperty("VILKAAR")
    private List<VilkaarDto> vilkaar;
}

