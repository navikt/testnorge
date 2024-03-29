package no.nav.testnav.apps.oppsummeringsdokumentservice.repository.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import no.nav.testnav.libs.dto.oppsummeringsdokumentservice.v2.Populasjon;

@Setter
@Getter
@Document(indexName = "#{@environment.getProperty('open.search.index')}")
public class OppsummeringsdokumentModel implements Persistable<String> {

    @Id
    private String id;
    @Field(type = FieldType.Date, format = DateFormat.year_month_day)
    private LocalDate kalendermaaned;
    private String opplysningspliktigOrganisajonsnummer;
    private Long version;
    @Field(type = FieldType.Nested, includeInParent = true)
    private List<VirksomhetModel> virksomheter;
    private String miljo;
    private String origin;
    private Populasjon populasjon;

    @CreatedDate
    @Field(type = FieldType.Date, format = DateFormat.basic_date_time)
    private Instant created;

    @LastModifiedDate
    @Field(type = FieldType.Date, format = DateFormat.basic_date_time)
    private Instant lastModified;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return id == null || created == null;
    }
}
