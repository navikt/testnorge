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
public class OpplysningDto {

    @JsonProperty("KODE")
    private String kode;

    @JsonProperty("OVERORDNET")
    private String overordnet;

    @JsonProperty("VERDI")
    private String verdi;
}

