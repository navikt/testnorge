package no.nav.testnav.libs.data.dollysearchservice.v2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.data.arbeidsplassencv.v1.ArbeidsplassenCVDTO;
import no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs.RsInntektsmelding;
import no.nav.testnav.libs.dto.skattekortservice.v1.SkattekortRequestDTO;
import no.nav.testnav.libs.dto.yrkesskade.v1.YrkesskadeRequest;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Document(indexName = "#{@environment.getProperty('open.search.index')}")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ElasticBestilling implements Persistable<Long> {

    @Id
    private Long id;
    @Field
    private Object krrstub;
    @Field
    private Object fullmakt;
    @Field
    private Object medl;
    @Field
    private Object instdata;
    @Field
    private Object aareg;
    @Field
    private Object sigrunstub;
    @Field
    private Object sigrunstubPensjonsgivende;
    @Field
    private Object inntektstub;
    @Field
    private Arenadata arenaforvalter;
    @Field
    private Object udistub;
    @Field
    private PensjonData pensjonforvalter;
    @Field
    private RsInntektsmelding inntektsmelding;
    @Field
    private Object brregstub;
    @Field
    private Object dokarkiv;
    @Field
    private Object histark;
    @Field
    private Object sykemelding;
    @Field
    private BankkontoData bankkonto;
    @Field
    private Object skjerming;
    @Field
    private ArbeidsplassenCVDTO arbeidsplassenCV;
    @Field
    private SkattekortRequestDTO skattekort;
    @Field
    private List<YrkesskadeRequest> yrkesskader;
    @Field
    private Object arbeidssoekerregisteret;
    @Field
    private List<String> identer;
    @Transient
    @JsonIgnore
    private boolean ignore;

    private static class BankkontoData {
        @Field
        private Object norskBankkonto;
        @Field
        private Object utenlandskBankkonto;
    }

    private static class PensjonData {
        @Field
        private Object inntekt;
        @Field
        private Object tp;
        @Field
        private Object alderspensjon;
        @Field
        private Object uforetrygd;
        @Field
        private Object afpOffentlig;
        @Field
        private Object pensjonsavtale;
    }

    private static class Arenadata {
        @Field
        private Object aap;
        @Field
        private Object aap115;
        @Field
        private Object dagpenger;
    }

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
