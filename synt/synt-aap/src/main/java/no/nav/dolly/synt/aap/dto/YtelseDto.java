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
public class YtelseDto {

    @JsonProperty("KODE")
    private String kode;

    @JsonProperty("VERDI")
    private String verdi;
}

