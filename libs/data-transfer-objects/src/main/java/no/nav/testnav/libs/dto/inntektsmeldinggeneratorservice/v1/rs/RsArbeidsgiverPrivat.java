package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RsArbeidsgiverPrivat {

    @Size(min = 11, max = 11)
    private String arbeidsgiverFnr;
    private RsKontaktinformasjon kontaktinformasjon;
}
