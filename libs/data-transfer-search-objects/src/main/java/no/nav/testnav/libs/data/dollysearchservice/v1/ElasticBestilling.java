package no.nav.testnav.libs.data.dollysearchservice.v1;

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
    private Object arenaforvalter;
    @Field
    private Object udistub;
    @Field
    private Object pensjonforvalter;
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
    private Object bankkonto;
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
