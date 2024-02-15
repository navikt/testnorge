package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RsArbeidsgiver {

    @Size(min = 9, max = 9)
    private String virksomhetsnummer;
    private RsKontaktinformasjon kontaktinformasjon;
}
