package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RsKontaktinformasjon {

    private String kontaktinformasjonNavn;
    @Size(min = 8, max = 8)
    private String telefonnummer;
}
