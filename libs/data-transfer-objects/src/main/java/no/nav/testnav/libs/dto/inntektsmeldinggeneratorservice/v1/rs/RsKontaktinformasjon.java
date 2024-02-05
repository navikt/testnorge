package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RsKontaktinformasjon {

    @JsonProperty
    private String kontaktinformasjonNavn;
    @JsonProperty
    @Size(min = 8, max = 8)
    private String telefonnummer;

}
