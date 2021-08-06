package no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter.tilleggsinformasjon;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Year;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonusFraForsvaret {

    @ApiModelProperty(
            example = "yyyy",
            dataType = "java.lang.String"
    )
    private Year aaretUtbetalingenGjelderFor;
}
