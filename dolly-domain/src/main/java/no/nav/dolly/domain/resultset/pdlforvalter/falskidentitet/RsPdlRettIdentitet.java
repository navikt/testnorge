package no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "identitetType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PdlRettIdentitetErUkjent.class, name = "UKJENT"),
        @JsonSubTypes.Type(value = PdlRettIdentitetVedIdentifikasjonsnummer.class, name = "ENTYDIG"),
        @JsonSubTypes.Type(value = RsPdlRettIdentitetVedOpplysninger.class, name = "OMTRENTLIG")
})
public abstract class RsPdlRettIdentitet {

}