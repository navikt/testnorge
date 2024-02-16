package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsArbeidsgiverPrivat {

    @Size(min = 11, max = 11)
    private String arbeidsgiverFnr;
    private RsKontaktinformasjon kontaktinformasjon;
}
