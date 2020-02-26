package no.nav.dolly.domain.resultset;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.aareg.RsArbeidsforhold;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.inntektstub.InntektMultiplierWrapper;
import no.nav.dolly.domain.resultset.inst.RsInstdata;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.domain.resultset.pdlforvalter.RsPdldata;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.domain.resultset.sigrunstub.OpprettSkattegrunnlag;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfUtvidetBestilling;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPerson;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RsDollyUpdateRequest {

    @ApiModelProperty(
            position = 1
    )
    private String ident;

    @ApiModelProperty(
            position = 2,
            value = "Liste av miljøer de(n) opprinnelig(e) bestilling(er) gjalt for",
            required = true
    )
    private List<String> environments;

    @ApiModelProperty(
            position = 3
    )
    private RsTpsfUtvidetBestilling tpsf;

    @ApiModelProperty(
            position = 4
    )
    private RsPdldata pdlforvalter;

    @ApiModelProperty(
            position = 5
    )
    private RsDigitalKontaktdata krrstub;

    @ApiModelProperty(
            position = 6
    )
    private List<RsInstdata> instdata;

    @ApiModelProperty(
            position = 7
    )
    private List<RsArbeidsforhold> aareg;

    @ApiModelProperty(
            position = 8
    )
    private List<OpprettSkattegrunnlag> sigrunstub;

    @ApiModelProperty(
            position = 9
    )
    private InntektMultiplierWrapper inntektstub;

    @ApiModelProperty(
            position = 10
    )
    private Arenadata arenaforvalter;

    @ApiModelProperty(
            position = 10
    )
    private RsUdiPerson udistub;

    @ApiModelProperty(
            position = 11
    )
    private PensjonData pensjonforvalter;

    public List<RsArbeidsforhold> getAareg() {
        if (isNull(aareg)) {
            aareg = new ArrayList<>();
        }
        return aareg;
    }

    public List<String> getEnvironments() {
        if (isNull(environments)) {
            environments = new ArrayList<>();
        }
        return environments;
    }

    public List<OpprettSkattegrunnlag> getSigrunstub() {
        if (isNull(sigrunstub)) {
            sigrunstub = new ArrayList<>();
        }
        return sigrunstub;
    }
}