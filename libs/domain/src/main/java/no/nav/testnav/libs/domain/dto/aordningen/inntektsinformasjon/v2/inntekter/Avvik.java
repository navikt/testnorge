package no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Avvik {

    @ApiModelProperty("unik id som identifiserer et eksisterende avvik, settes til null om det skal opprettes et nytt avvik")
    private Long id;
    @ApiModelProperty(
            value = "tidligere har default verdier fra avvik på inntektsinformsjonen vært 'MAGNET_EDAG-110' og på inntekten 'MAGNET_EDAG-116B'",
            position = 1
    )
    private String navn;
    @ApiModelProperty(
            value = "tidligere har default verdier fra avvik på inntektsinformsjonen vært 'RETNINGSLINJE' og på inntekten 'OEYEBLIKKELIG'",
            position = 2
    )
    private Alvorlighetsgrad alvorlighetsgrad;
}
