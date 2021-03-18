package no.nav.registre.testnorge.oppsummeringsdokumentservice.repository.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;

@Getter
@Setter
public class InntektModel {
    @Field(type = FieldType.Date, format = DateFormat.year_month_day)
    private LocalDate startdatoOpptjeningsperiode;
    @Field(type = FieldType.Date, format = DateFormat.year_month_day)
    private LocalDate sluttdatoOpptjeningsperiode;
    private Integer antall;
    private String opptjeningsland;
}
