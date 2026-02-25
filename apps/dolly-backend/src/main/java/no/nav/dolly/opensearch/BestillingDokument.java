package no.nav.dolly.opensearch;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.bestilling.skattekort.domain.SkattekortRequest;
import no.nav.dolly.domain.resultset.RsNomData;
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
import no.nav.dolly.domain.resultset.skjerming.RsSkjerming;
import no.nav.dolly.domain.resultset.sykemelding.RsSykemelding;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPerson;
import no.nav.testnav.libs.dto.arbeidsplassencv.v1.ArbeidsplassenCVDTO;
import no.nav.testnav.libs.dto.yrkesskade.v1.YrkesskadeRequest;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BestillingDokument implements Persistable<Long> {

    @Id
    private Long id;
    
    private PdlPersondata pdldata;
    
    private RsDigitalKontaktdata krrstub;
    
    private List<RsFullmakt> fullmakt;
    
    private RsMedl medl;
    
    private List<RsInstdata> instdata;
    
    private List<RsAareg> aareg;
    
    private List<RsLignetInntekt> sigrunstub;
    
    private List<RsPensjonsgivendeForFolketrygden> sigrunstubPensjonsgivende;
    
    private List<RsSummertSkattegrunnlag> sigrunstubSummertSkattegrunnlag;
    
    private InntektMultiplierWrapper inntektstub;
    
    private Arenadata arenaforvalter;
    
    private RsUdiPerson udistub;
    
    private PensjonData pensjonforvalter;
    
    private RsInntektsmelding inntektsmelding;
    
    private RsBregdata brregstub;
    
    private List<RsDokarkiv> dokarkiv;
    
    private RsHistark histark;
    
    private RsSykemelding sykemelding;
    
    private BankkontoData bankkonto;
    
    private RsSkjerming skjerming;
    
    private ArbeidsplassenCVDTO arbeidsplassenCV;
    
    private SkattekortRequest skattekort;
    
    private List<YrkesskadeRequest> yrkesskader;
    
    private RsArbeidssoekerregisteret arbeidssoekerregisteret;
    
    private List<String> identer;
    
    private List<String> miljoer;
    
    private List<EtterlatteYtelse> etterlatteYtelser;
    
    private RsNomData nomdata;

    @Transient
    @JsonIgnore
    private boolean ignore;

    @Override
    @JsonIgnore
    @Transient
    public boolean isNew() {

        return false;
    }

    public List<String> getIdenter() {
        if (isNull(identer)) {
            identer = new ArrayList<>();
        }
        return identer;
    }
}
