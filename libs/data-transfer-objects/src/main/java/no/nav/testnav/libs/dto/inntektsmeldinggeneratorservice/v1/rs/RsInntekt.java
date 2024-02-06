package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RsInntekt {

    @JsonProperty
    private Double beloep;
    @JsonProperty
    private String aarsakVedEndring;
}
