package no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "rettIdentitetType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PdlIdentitetErUkjent.class, name = "ER_UKJENT"),
        @JsonSubTypes.Type(value = PdlIdentitetVedIdentifikasjonsnummer.class, name = "VED_IDENTIFIKASJONSNUMMER"),
        @JsonSubTypes.Type(value = RsPdlRettIdentitetVedOpplysninger.class, name = "VED_OPPLYSNINGER")
})
public abstract class RsPdlRettIdentitet {

}