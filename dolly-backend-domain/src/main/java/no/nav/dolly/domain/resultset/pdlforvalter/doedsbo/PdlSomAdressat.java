package no.nav.dolly.domain.resultset.pdlforvalter.doedsbo;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "adressatType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PdlAdvokat.class, name = "ADVOKAT"),
        @JsonSubTypes.Type(value = PdlKontaktpersonMedIdNummer.class, name = "PERSON_MEDID"),
        @JsonSubTypes.Type(value = RsPdlKontaktpersonUtenIdNummer.class, name = "PERSON_UTENID"),
        @JsonSubTypes.Type(value = PdlOrganisasjon.class, name = "ORGANISASJON")
})
@Getter
@Setter
public abstract class PdlSomAdressat {

    @ApiModelProperty(
            position = 1,
            required = true,
            value = "AdressatType kan ha en følgende verdier: \n" + ""
                    + "ADVOKAT, ORGANISASJON, PERSON_MEDID, PERSON_UTENID\n\n" +

                    "For advokat eller organisasjon settes:\n" +
                    "\"adressat\": {\n" +
                    "  \"adressatType\": \"ADVOKAT\"/\"ORGANISASJON\",\n" +
                    "  \"kontaktperson\": {\n" +
                    "    \"etternavn\": \"string\"\n" +
                    "    \"fornavn\": \"string\"\n" +
                    "    \"mellomnavn\": \"string\"\n" +
                    "  },\n" +
                    "  \"organisajonsnavn\": \"string\",\n" +
                    "  \"organisajonsnummer\": \"string\"\n" +
                    "}\n\n" +

                    "For kontaktperson med ID:\n" +
                    "\"adressat\": {\n" +
                    "\"adressatType\": \"PERSON_MEDID\",\n" +
                    "\"idnummer\": \"string\"\n" +
                    "}\n\n" +

                    "For kontaktperson uten ID:\n" +
                    "\"adressat\":{\n" +
                    "\"adressatType\":\"PERSON_UTENID\",\n" +
                    "\"navn\":{\n" +
                    "\"etternavn\":\"string\",\n" +
                    "\"fornavn\":\"string\",\n" +
                    "\"mellomnavn\": \"string\"\n" +
                    "},\n" +
                    "\"foedselsdato\": \"string($date-time)\"\n" +
                    "}"
    )
    private String adressatType;
}