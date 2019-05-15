package no.nav.dolly.domain.resultset.arenaforvalter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "brukertype")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RsArenaBrukerUtenServicebehov.class, name = "UTEN_SERVICE_BEHOV"),
        @JsonSubTypes.Type(value = RsArenaBrukerMedServicebehov.class, name = "MED_SERVICE_BEHOV")
})

public interface RsArenadata {

}