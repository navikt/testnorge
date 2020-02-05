package no.nav.dolly.domain.resultset.aareg;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "aktoertype")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RsOrganisasjon.class, name = "ORG"),
        @JsonSubTypes.Type(value = RsAktoerPerson.class, name = "PERS")
})
@Getter
@Setter
public abstract class RsAktoer {

    @ApiModelProperty(value = "Type av aktør ORG eller PERS\n" +
            "For organisajon:\n" +
            "\"arbeidsgiver\":{\n" +
            "\"aktoertype\": \"ORG\",\n" +
            "\"orgnummer\": \"999999999\"\n" +
            "}\n" +
            "For person:\n" +
            "\"arbeidsgiver\": {\n" +
            "\"aktoertype\": \"PERS\",\n" +
            "\"ident\": \"12345678901\",\n" +
            "}",
            required = true)
    private String aktoertype;
}