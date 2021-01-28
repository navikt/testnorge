package no.nav.registre.testnorge.arbeidsforhold.repository.elastic.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Getter
@Setter
public class PersonModel {
    private String ident;
    @Field(type = FieldType.Nested, includeInParent = true)
    private List<ArbeidsforholdModel> arbeidsforhold;
}
