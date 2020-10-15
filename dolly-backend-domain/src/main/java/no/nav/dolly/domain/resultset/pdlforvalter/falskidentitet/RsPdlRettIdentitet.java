package no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "identitetType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PdlRettIdentitetErUkjent.class, name = "UKJENT"),
        @JsonSubTypes.Type(value = PdlRettIdentitetVedIdentifikasjonsnummer.class, name = "ENTYDIG"),
        @JsonSubTypes.Type(value = RsPdlRettIdentitetVedOpplysninger.class, name = "OMTRENTLIG")
})
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class RsPdlRettIdentitet {

    @Schema(required = true,
            description = "identitetType kan en av følgende verdier:\n" +
                    "UKJENT, ENTYDIG, OMTRENTLIG\n\n" +
                    "For identitet ukjent:\n" +
                    "  \"rettIdentitet\": {\n" +
                    "    \"identitetType\": \"UKJENT\",\n" +
                    "    \"rettIdentitetErUkjent\": true\n" +
                    "  }\n\n" +

                    "For identitet med personnummer:\n" +
                    "  \"rettIdentitet\": {\n" +
                    "    \"identitetType\": \"ENTYDIG\",\n" +
                    "    \"rettIdentitetVedIdentifikasjonsnummer\": \"FNR\"/\"DNR\"/\"BOST\"\n" +
                    "  }\n\n" +

                    "For identitet med opplysninger:\n" +
                    "  \"rettIdentitet\": {\n" +
                    "    \"identitetType\": \"OMTRENTLIG\",\n" +
                    "    \"foedselsdato\": \"string($date-time)\"\n" +
                    "    \"kjoenn\": \"MANN\"/\"KVINNE\"/\"UKJENT\"\n" +
                    "    \"personnavn\":{\n" +
                    "      \"etternavn\": \"string\",\n" +
                    "      \"fornavn\": \"string\",\n" +
                    "      \"mellomnavn\": “string”\n" +
                    "    },\n" +
                    "    \"statsborgerskap\": \"string\" i hht kodeverk 'Landkoder'\n" +
                    "  }"
    )
    private String identitetType;

    public abstract String getIdentitetType();
}