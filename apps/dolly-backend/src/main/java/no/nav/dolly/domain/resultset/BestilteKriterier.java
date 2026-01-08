package no.nav.dolly.domain.resultset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.arbeidssoekerregistrering.RsArbeidssoekerregisteret;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.breg.RsBregdata;
import no.nav.dolly.domain.resultset.dokarkiv.RsDokarkiv;
import no.nav.dolly.domain.resultset.etterlatte.EtterlatteYtelse;
import no.nav.dolly.domain.resultset.fullmakt.RsFullmakt;
import no.nav.dolly.domain.resultset.histark.RsHistark;
import no.nav.dolly.domain.resultset.inntektsmeldingstub.RsInntektsmelding;
import no.nav.dolly.domain.resultset.inntektstub.InntektMultiplierWrapper;
import no.nav.dolly.domain.resultset.inst.RsInstdata;
import no.nav.dolly.domain.resultset.kontoregister.BankkontoData;
import no.nav.dolly.domain.resultset.krrstub.RsDigitalKontaktdata;
import no.nav.dolly.domain.resultset.medl.RsMedl;
import no.nav.dolly.domain.resultset.pdldata.PdlPersondata;
import no.nav.dolly.domain.resultset.pensjon.PensjonData;
import no.nav.dolly.domain.resultset.sigrunstub.RsLignetInntekt;
import no.nav.dolly.domain.resultset.sigrunstub.RsPensjonsgivendeForFolketrygden;
import no.nav.dolly.domain.resultset.sigrunstub.RsSummertSkattegrunnlag;
import no.nav.dolly.domain.resultset.skattekort.SkattekortRequestDTO;
import no.nav.dolly.domain.resultset.skjerming.RsSkjerming;
import no.nav.dolly.domain.resultset.sykemelding.RsSykemelding;
import no.nav.dolly.domain.resultset.tpsmessagingservice.RsTpsMessaging;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPerson;
import no.nav.testnav.libs.dto.arbeidsplassencv.v1.ArbeidsplassenCVDTO;
import no.nav.testnav.libs.dto.yrkesskade.v1.YrkesskadeRequest;
import com.fasterxml.jackson.annotation.JsonInclude;

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
    private RsUdiPerson udistub;
    private List<RsLignetInntekt> sigrunstub;
    private List<RsPensjonsgivendeForFolketrygden> sigrunstubPensjonsgivende;
    private List<RsSummertSkattegrunnlag> sigrunstubSummertSkattegrunnlag;
    private Arenadata arenaforvalter;
    private PdlPersondata pdldata;
    private List<RsInstdata> instdata;
    private InntektMultiplierWrapper inntektstub;
    private PensjonData pensjonforvalter;
    private RsInntektsmelding inntektsmelding;
    private RsBregdata brregstub;
    private List<RsDokarkiv> dokarkiv;
    private List<RsFullmakt> fullmakt;
    private RsMedl medl;
    private RsHistark histark;
    private RsTpsMessaging tpsMessaging;
    private BankkontoData bankkonto;
    private RsSkjerming skjerming;
    private RsSykemelding sykemelding;
    private ArbeidsplassenCVDTO arbeidsplassenCV;
    private SkattekortRequestDTO skattekort;
    private List<YrkesskadeRequest> yrkesskader;
    private RsArbeidssoekerregisteret arbeidssoekerregisteret;
    private List<EtterlatteYtelse> etterlatteYtelser;
    private RsNomData nomdata;
}
