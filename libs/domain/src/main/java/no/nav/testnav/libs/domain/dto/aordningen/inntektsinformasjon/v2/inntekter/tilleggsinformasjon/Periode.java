package no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.tilleggsinformasjon;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Periode {

    @ApiModelProperty(
            example = "yyyy-MM-dd",
            position = 0
    )
    private LocalDate startdato;
    @ApiModelProperty(
            example = "yyyy-MM-dd",
            position = 1
    )
    private LocalDate sluttdato;
}
