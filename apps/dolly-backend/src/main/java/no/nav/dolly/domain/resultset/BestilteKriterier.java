package no.nav.dolly.domain.resultset;

import com.fasterxml.jackson.annotation.JsonInclude;
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
import no.nav.dolly.domain.resultset.nom.RsNomData;
import no.nav.dolly.domain.resultset.pdldata.PdlPersondata;
import no.nav.dolly.domain.resultset.pdlforvalter.RsPdldata;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.domain.resultset.sigrunstub.OpprettSkattegrunnlag;
import no.nav.dolly.domain.resultset.skjerming.RsSkjerming;
import no.nav.dolly.domain.resultset.sykemelding.RsSykemelding;
import no.nav.dolly.domain.resultset.tpsmessagingservice.RsTpsMessaging;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPerson;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(value = Include.NON_EMPTY)
public class BestilteKriterier {

    private List<RsAareg> aareg;
    private RsDigitalKontaktdata krrstub;
    private RsNomData nomData;
    private RsUdiPerson udistub;
    private List<OpprettSkattegrunnlag> sigrunstub;
    private Arenadata arenaforvalter;
    private RsPdldata pdlforvalter;
    private PdlPersondata pdldata;
    private List<RsInstdata> instdata;
    private InntektMultiplierWrapper inntektstub;
    private PensjonData pensjonforvalter;
    private RsInntektsmelding inntektsmelding;
    private RsBregdata brregstub;
    private RsDokarkiv dokarkiv;
    private RsTpsMessaging tpsMessaging;
    private RsSkjerming skjerming;
    private RsSykemelding sykemelding;
}
