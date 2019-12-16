package no.nav.dolly.domain.resultset;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsforhold;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.inntektstub.RsInntektsinformasjon;
import no.nav.dolly.domain.resultset.inst.RsInstdata;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.domain.resultset.pdlforvalter.RsPdldata;
import no.nav.dolly.domain.resultset.sigrunstub.OpprettSkattegrunnlag;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPerson;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsDollyBestilling {

    @ApiModelProperty(
            position = 1,
            value = "Liste av miljøer bestillingen gjelder for",
            required = true
    )
    private List<String> environments;

    @ApiModelProperty(
            position = 2,
            value = "Navn på malbestillling"
    )
    private String malBestillingNavn;

    @ApiModelProperty(
            position = 3
    )
    private RsPdldata pdlforvalter;

    @ApiModelProperty(
            position = 4
    )
    private RsDigitalKontaktdata krrstub;

    @ApiModelProperty(
            position = 5
    )
    private List<RsInstdata> instdata;

    @ApiModelProperty(
            position = 6
    )
    private List<RsArbeidsforhold> aareg;

    @ApiModelProperty(
            position = 7
    )
    private List<OpprettSkattegrunnlag> sigrunstub;

    @ApiModelProperty(
            position = 8
    )
    private RsInntektsinformasjon inntektstub;

    @ApiModelProperty(
            position = 9
    )
    private Arenadata arenaforvalter;

    @ApiModelProperty(
            position = 10
    )
    private RsUdiPerson udistub;

    public List<RsArbeidsforhold> getAareg() {
        if (isNull(aareg)) {
            aareg = new ArrayList();
        }
        return aareg;
    }

    public List<String> getEnvironments() {
        if (isNull(environments)) {
            environments = new ArrayList();
        }
        return environments;
    }

    public List<OpprettSkattegrunnlag> getSigrunstub() {
        if (isNull(sigrunstub)) {
            sigrunstub = new ArrayList();
        }
        return sigrunstub;
    }

    public List<RsInstdata> getInstdata() {
        if (isNull(instdata)) {
            instdata = new ArrayList();
        }
        return instdata;
    }
}
