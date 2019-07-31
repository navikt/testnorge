package no.nav.dolly.domain.resultset.pdlforvalter.falskidentitet;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "identitetType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PdlIdentitetErUkjent.class, name = "UKJENT"),
        @JsonSubTypes.Type(value = PdlIdentitetVedIdentifikasjonsnummer.class, name = "NORSK"),
        @JsonSubTypes.Type(value = PdlRettIdentitetVedOpplysninger.class, name = "UTENLANDSK")
})
public abstract class PdlRettIdentitet {

}