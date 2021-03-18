package no.nav.registre.inntektsmeldinggeneratorservice.provider.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

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
