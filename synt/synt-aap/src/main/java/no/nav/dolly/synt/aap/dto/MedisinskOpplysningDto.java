package no.nav.dolly.synt.aap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedisinskOpplysningDto {

    @JsonProperty("DIAGNOSE")
    private String diagnose;

    @JsonProperty("KILDE")
    private String kilde;

    @JsonProperty("KILDEDATO")
    private String kildedato;

    @JsonProperty("KLASSIFISERING")
    private String klassifisering;

    @JsonProperty("TYPE")
    private String type;
}

