package no.nav.skattekortservice.dto.v2;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HentSkattekortRequest {

    @NotBlank
    @Size(min = 11, max = 11)
    @Pattern(regexp = "^\\d{11}$")
    private String fnr;

    private Integer inntektsaar;
}
