package no.nav.dolly.domain.resultset.aareg;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "aktoertype")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RsOrganisasjon.class, name = "ORG"),
        @JsonSubTypes.Type(value = RsAktoerPerson.class, name = "PERS")
})
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class RsAktoer {

    @Schema(description = "Type av aktør er ORG eller PERS for organisasjon eller person",
            required = true)

    private String aktoertype;

    public abstract String getAktoertype();
}