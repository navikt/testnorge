package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RsKontaktinformasjon {

    @JsonProperty
    private String kontaktinformasjonNavn;
    @JsonProperty
    @Size(min = 8, max = 8)
    private String telefonnummer;

}
