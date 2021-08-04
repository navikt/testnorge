package no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Forskuddstrekk {

    @ApiModelProperty(
            value = "unik id som identifiserer et eksisterende forskuddstrekk, settes til null om det skal opprettes et nytt forskuddstrekk",
            position = 0
    )
    private Long id;
    @ApiModelProperty(
            value = "gyldige verdier ligger i kodeverket 'Forskuddstrekkbeskrivelse'",
            position = 1
    )
    private String beskrivelse;
    @ApiModelProperty(
            value = "",
            position = 2
    )
    private double beloep;
    @ApiModelProperty(
            value = "",
            position = 3
    )
    private List<Avvik> avvik;
    @ApiModelProperty(
            value = "menneskelig lesbar feilmelding. Ligge kun i responsen fra inntektstub om noe er galt med dette objektet.",
            position = 4
    )
    private String feilmelding;
}
