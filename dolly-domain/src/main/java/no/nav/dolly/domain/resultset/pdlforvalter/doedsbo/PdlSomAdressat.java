package no.nav.dolly.domain.resultset.pdlforvalter.doedsbo;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "adressatType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PdlAdvokat.class, name = "ADVOKAT"),
        @JsonSubTypes.Type(value = PdlKontaktpersonMedIdNummer.class, name = "PERSON_MEDID"),
        @JsonSubTypes.Type(value = RsPdlKontaktpersonUtenIdNummer.class, name = "PERSON_UTENID"),
        @JsonSubTypes.Type(value = PdlOrganisasjon.class, name = "ORGANISASJON")
})
public abstract class PdlSomAdressat {

}