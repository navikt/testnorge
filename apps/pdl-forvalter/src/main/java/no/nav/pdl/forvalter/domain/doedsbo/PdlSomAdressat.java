package no.nav.pdl.forvalter.domain.doedsbo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "adressatType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PdlAdvokat.class, name = "ADVOKAT"),
        @JsonSubTypes.Type(value = PdlKontaktpersonMedIdNummer.class, name = "PERSON_MEDID"),
        @JsonSubTypes.Type(value = RsPdlKontaktpersonUtenIdNummer.class, name = "PERSON_UTENID"),
        @JsonSubTypes.Type(value = PdlOrganisasjon.class, name = "ORGANISASJON")
})
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class PdlSomAdressat implements Serializable {

    @Schema(required = true,
            description = "AdressatType må settes hhv ADVOKAT, PERSON_MEDID, PERSON_UTENID, ORGANISASJON")
    private String adressatType;

    public abstract String getAdressatType();
}