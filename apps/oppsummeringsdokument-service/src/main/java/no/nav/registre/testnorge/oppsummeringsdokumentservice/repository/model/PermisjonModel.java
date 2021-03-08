package no.nav.registre.testnorge.oppsummeringsdokumentservice.repository.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;

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
}
