package no.nav.dolly.domain.resultset.pdlforvalter.doedsbo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "adressatType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RsPdlAdvokat.class, name = "ADVOKAT"),
        @JsonSubTypes.Type(value = RsPdlKontaktpersonMedIdNummer.class, name = "PERSON_MEDID"),
        @JsonSubTypes.Type(value = RsRsPdlKontaktpersonUtenIdNummer.class, name = "PERSON_UTENID"),
        @JsonSubTypes.Type(value = RsPdlOrganisasjon.class, name = "ORGANISASJON")
})
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class RsPdlAdressat {

    @Schema(required = true,
            description = "AdressatType må settes hhv ADVOKAT, PERSON_MEDID, PERSON_UTENID, ORGANISASJON")
    private String adressatType;

    public abstract String getAdressatType();
}