package no.nav.testnav.apps.oppsummeringsdokumentservice.repository.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class PermisjonModel {
    private String permisjonId;
    private String beskrivelse;
    @Field(type = FieldType.Date, format = DateFormat.year_month_day)
    private LocalDate startdato;
    @Field(type = FieldType.Date, format = DateFormat.year_month_day)
    private LocalDate sluttdato;
    private Float permisjonsprosent;
    @Field(type = FieldType.Nested, includeInParent = true)
    private List<AvvikModel> avvik;
}
