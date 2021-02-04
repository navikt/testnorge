package no.nav.registre.testnorge.arbeidsforhold.repository.elastic.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ArbeidsforholdModel {
    private String arbeidsforholdId;
    private String typeArbeidsforhold;
    @Field(type = FieldType.Date, format = DateFormat.basic_date)
    private LocalDate startdato;
    @Field(type = FieldType.Date, format = DateFormat.basic_date)
    private LocalDate sluttdato;
    private Float antallTimerPerUke;
    private String yrke;
    private String arbeidstidsordning;
    private Float stillingsprosent;
    @Field(type = FieldType.Date, format = DateFormat.basic_date)
    private LocalDate sisteLoennsendringsdato;
    private List<ArbeidsforholdModel> arbeidsforhold;
}
