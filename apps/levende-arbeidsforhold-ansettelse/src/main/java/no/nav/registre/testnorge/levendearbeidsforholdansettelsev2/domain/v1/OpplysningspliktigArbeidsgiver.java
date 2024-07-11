package no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.domain.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Organisasjon.class, name = "Organisasjon"),
        @JsonSubTypes.Type(value = Person.class, name = "Person")
})
@Schema(description = "Informasjon om opplysningspliktig eller arbeidsgiver (organisasjon eller person)", oneOf = {Organisasjon.class, Person.class})
@SuppressWarnings("squid:S1610")
public abstract class OpplysningspliktigArbeidsgiver {

    public abstract String getType();
}
