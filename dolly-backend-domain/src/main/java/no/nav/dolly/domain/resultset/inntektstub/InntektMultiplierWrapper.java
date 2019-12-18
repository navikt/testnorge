package no.nav.dolly.domain.resultset.inntektstub;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InntektMultiplierWrapper {

    @ApiModelProperty(
            position = 1,
            dataType = "Integer",
            value = "Antall måneder som skal preppes basert på første element i inntektsinformasjon",
            example = "36")
    private Integer antallMaaneder;

    @ApiModelProperty(
            position = 2,
            dataType = "Float",
            value = "Prosent lønnsøkning per år som feltet beloep skal økes med",
            example = "2.5")
    private Double prosentOekningPerAaar;

    @ApiModelProperty(
            position = 3)
    private List<RsInntektsinformasjon> inntektsinformasjon;

    public List<RsInntektsinformasjon> getInntektsinformasjon() {
        if (isNull(inntektsinformasjon)) {
            inntektsinformasjon = new ArrayList();
        }
        return inntektsinformasjon;
    }
}
