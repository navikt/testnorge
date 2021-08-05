package no.nav.testnav.libs.domain.dto.aordningen.inntektsinformasjon.v2.inntekter;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.YearMonth;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inntektsinformasjon {

    @ApiModelProperty(
            required = true,
            position = 0
    )
    private String norskIdent;
    @ApiModelProperty(
            value = "året/måneden inntektsinformasjonen gjelder for",
            example = "yyyy-MM",
            dataType = "java.lang.String",
            required = true,
            position = 1
    )
    private YearMonth aarMaaned;
    @ApiModelProperty(
            value = "organisasjonsnummer/norskIdent",
            required = true,
            position = 2
    )
    private String opplysningspliktig;
    @ApiModelProperty(
            value = "organisasjonsnummer/norskIdent",
            required = true,
            position = 3
    )
    private String virksomhet;
    @ApiModelProperty(
            position = 4
    )
    private List<Inntekt> inntektsliste;
    @ApiModelProperty(
            position = 5
    )
    private List<Fradrag> fradragsliste;
    @ApiModelProperty(
            position = 6
    )
    private List<Forskuddstrekk> forskuddstrekksliste;
    @ApiModelProperty(
            position = 7
    )
    private List<Arbeidsforhold> arbeidsforholdsliste;
    @ApiModelProperty(
            value = "",
            position = 8
    )
    private List<Avvik> avvik;
    @ApiModelProperty(
            value = "Versjonsnummeret til historikken, 'null' er nåværende versjon",
            position = 9
    )
    private Long versjon;
    @ApiModelProperty(
            value = "Menneskelig lesbar feilmelding. Ligger kun i responsen fra inntektstub om noe er galt med dette objektet.",
            position = 10
    )
    private String feilmelding;
}
