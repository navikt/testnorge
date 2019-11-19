package no.nav.dolly.domain.resultset.aareg;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "aktoertype")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RsOrganisasjon.class, name = "ORG"),
        @JsonSubTypes.Type(value = RsAktoerPerson.class, name = "PERS")
})

@ApiOperation(
        value = "For organisajon:\n" +
                "\"aktoertype\": \"ORG\",\n" +
                "\"orgnummer\": \"999999999\"\n" +
                "}\n" +
                "For person:\n" +
                "\"aktoer\": {\n" +
                "\"aktoertype\": \"PERS\",\n" +
                "\"ident\": \"12345678901\",\n" +
                "\"identtype\": \"FNR/DNR/BOST\"\n" +
                "}")
public abstract class RsAktoer {

    @ApiModelProperty(value = "Type av aktør ORG eller PERS",
            required = true,
            position = 0)
    private String aktoertype;
}