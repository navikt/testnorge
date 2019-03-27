package no.nav.dolly.domain.resultset.aareg;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "aktoertype")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RsOrganisasjon.class, name = "ORG"),
        @JsonSubTypes.Type(value = RsAktoerPerson.class, name = "PERS")
})
public abstract class RsAktoer {

}