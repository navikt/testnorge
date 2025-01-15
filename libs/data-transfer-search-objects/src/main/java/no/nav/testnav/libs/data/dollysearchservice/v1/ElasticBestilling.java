package no.nav.testnav.libs.data.dollysearchservice.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.testnav.libs.data.arbeidsplassencv.v1.ArbeidsplassenCVDTO;
import no.nav.testnav.libs.data.pdlforvalter.v1.PersonDTO;
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
    private PersonDTO pdldata;
//    @Field
//    private RsDigitalKontaktdata krrstub;
//    @Field
//    private List<RsFullmakt> fullmakt;
//    @Field
//    private RsMedl medl;
//    @Field
//    private List<RsInstdata> instdata;
//    @Field
//    private List<RsAareg> aareg;
//    @Field
//    private List<RsLignetInntekt> sigrunstub;
//    @Field
//    private List<RsPensjonsgivendeForFolketrygden> sigrunstubPensjonsgivende;
//    @Field
//    private InntektMultiplierWrapper inntektstub;
//    @Field
//    private Arenadata arenaforvalter;
//    @Field
//    private RsUdiPerson udistub;
//    @Field
//    private PensjonData pensjonforvalter;
//    @Field
//    private RsInntektsmelding inntektsmelding;
//    @Field
//    private RsBregdata brregstub;
//    @Field
//    private RsDokarkiv dokarkiv;
//    @Field
//    private RsHistark histark;
//    @Field
//    private RsSykemelding sykemelding;
//    @Field
//    private BankkontoData bankkonto;
//    @Field
//    private RsSkjerming skjerming;
    @Field
    private ArbeidsplassenCVDTO arbeidsplassenCV;
    @Field
    private SkattekortRequestDTO skattekort;
    @Field
    private List<YrkesskadeRequest> yrkesskader;
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
