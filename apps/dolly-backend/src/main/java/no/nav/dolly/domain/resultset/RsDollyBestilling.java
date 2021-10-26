package no.nav.dolly.domain.resultset;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.breg.RsBregdata;
import no.nav.dolly.domain.resultset.dokarkiv.RsDokarkiv;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.RsInntektsmelding;
import no.nav.dolly.domain.resultset.inntektstub.InntektMultiplierWrapper;
import no.nav.dolly.domain.resultset.inst.RsInstdata;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.domain.resultset.pdldata.PdlPersondata;
import no.nav.dolly.domain.resultset.pdlforvalter.RsPdldata;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.domain.resultset.sigrunstub.OpprettSkattegrunnlag;
import no.nav.dolly.domain.resultset.sykemelding.RsSykemelding;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPerson;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RsDollyBestilling {

    @Schema(description = "Liste av miljøer bestillingen skal deployes til")
    private List<String> environments;

    @Schema(description = "Navn på malbestillling")
    private String malBestillingNavn;

    private RsPdldata pdlforvalter;

    private PdlPersondata pdldata;

    private RsDigitalKontaktdata krrstub;

    private List<RsInstdata> instdata;

    private List<RsAareg> aareg;

    private List<OpprettSkattegrunnlag> sigrunstub;

    private InntektMultiplierWrapper inntektstub;

    private Arenadata arenaforvalter;

    private RsUdiPerson udistub;

    private PensjonData pensjonforvalter;

    private RsInntektsmelding inntektsmelding;

    private RsBregdata brregstub;

    private RsDokarkiv dokarkiv;

    private RsSykemelding sykemelding;

    public List<RsAareg> getAareg() {
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

    public List<RsInstdata> getInstdata() {
        if (isNull(instdata)) {
            instdata = new ArrayList<>();
        }
        return instdata;
    }
}
