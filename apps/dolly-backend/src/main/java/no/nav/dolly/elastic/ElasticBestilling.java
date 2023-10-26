package no.nav.dolly.elastic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.arenaforvalter.Arenadata;
import no.nav.dolly.domain.resultset.breg.RsBregdata;
import no.nav.dolly.domain.resultset.dokarkiv.RsDokarkiv;
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
import no.nav.dolly.domain.resultset.skjerming.RsSkjerming;
import no.nav.dolly.domain.resultset.sykemelding.RsSykemelding;
import no.nav.dolly.domain.resultset.tpsmessagingservice.RsTpsMessaging;
import no.nav.dolly.domain.resultset.udistub.model.RsUdiPerson;
import no.nav.testnav.libs.data.arbeidsplassencv.v1.ArbeidsplassenCVDTO;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Objects.isNull;

@Document(indexName = "bestilling")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ElasticBestilling implements Persistable<Long> {

    @Id
    private Long id;

    @Field
    private Set<String> environments;
    @Field
    private String malBestillingNavn;
    @Field
    private PdlPersondata pdldata;
    @Field
    private RsDigitalKontaktdata krrstub;
    @Field
    private RsMedl medl;
    @Field
    private List<RsInstdata> instdata;
    @Field
    private List<RsAareg> aareg;
    @Field
    private List<RsLignetInntekt> sigrunstub;
    @Field
    private List<RsPensjonsgivendeForFolketrygden> sigrunstubPensjonsgivende;
    @Field
    private InntektMultiplierWrapper inntektstub;
    @Field
    private Arenadata arenaforvalter;
    @Field
    private RsUdiPerson udistub;
    @Field
    private PensjonData pensjonforvalter;
    @Field
    private RsInntektsmelding inntektsmelding;
    @Field
    private RsBregdata brregstub;
    @Field
    private RsDokarkiv dokarkiv;
    @Field
    private RsHistark histark;
    @Field
    private RsSykemelding sykemelding;
    @Field
    private RsTpsMessaging tpsMessaging;
    @Field
    private BankkontoData bankkonto;
    @Field
    private RsSkjerming skjerming;
    @Field
    private ArbeidsplassenCVDTO arbeidsplassenCV;
    @Field
    private List<String> identer;

    public List<RsAareg> getAareg() {
        if (isNull(aareg)) {
            aareg = new ArrayList<>();
        }
        return aareg;
    }

    public Set<String> getEnvironments() {
        if (isNull(environments)) {
            environments = new HashSet<>();
        }
        return environments;
    }

    public List<RsLignetInntekt> getSigrunstub() {
        if (isNull(sigrunstub)) {
            sigrunstub = new ArrayList<>();
        }
        return sigrunstub;
    }

    public List<RsPensjonsgivendeForFolketrygden> getSigrunstubPensjonsgivende() {
        if (isNull(sigrunstubPensjonsgivende)) {
            sigrunstubPensjonsgivende = new ArrayList<>();
        }
        return sigrunstubPensjonsgivende;
    }

    public List<RsInstdata> getInstdata() {
        if (isNull(instdata)) {
            instdata = new ArrayList<>();
        }
        return instdata;
    }

    public List<String> getIdenter() {
        if (isNull(identer)) {
            identer = new ArrayList<>();
        }
        return identer;
    }

    @Override
    @JsonIgnore
    public boolean isNew() {

        return true;
    }
}
