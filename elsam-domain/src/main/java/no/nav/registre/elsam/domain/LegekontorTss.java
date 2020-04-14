package no.nav.registre.elsam.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LegekontorTss {

    private String avdelingsnr;
    private String avdelingsnavn;
    private String typeAvdeling;
    @JsonProperty("avdOffnr")
    private String eregId;
}
