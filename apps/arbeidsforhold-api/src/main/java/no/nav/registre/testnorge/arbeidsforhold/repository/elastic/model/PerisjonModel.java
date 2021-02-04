package no.nav.registre.testnorge.arbeidsforhold.repository.elastic.model;

import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;

public class PerisjonModel {
    private String beskrivelse;
    @Field(type = FieldType.Date, format = DateFormat.basic_date)
    private LocalDate startdato;
    @Field(type = FieldType.Date, format = DateFormat.basic_date)
    private LocalDate sluttdato;
    private Float permisjonsprosent;
}
