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
public class InntektModel {
    @Field(type = FieldType.Date, format = DateFormat.year_month_day)
    private LocalDate startdatoOpptjeningsperiode;
    @Field(type = FieldType.Date, format = DateFormat.year_month_day)
    private LocalDate sluttdatoOpptjeningsperiode;
    private Integer antall;
    private String opptjeningsland;
    @Field(type = FieldType.Nested, includeInParent = true)
    private List<AvvikModel> avvik;
}
