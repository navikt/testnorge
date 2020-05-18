package no.nav.registre.inntekt.domain.altinn.rs;

import static no.nav.registre.inntekt.utils.CommonConstants.TYPE_ORGANISASJON;
import static no.nav.registre.inntekt.utils.CommonConstants.TYPE_PERSON;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "aktoertype")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RsArbeidsgiver.class, name = TYPE_ORGANISASJON),
        @JsonSubTypes.Type(value = RsArbeidsgiverPrivat.class, name = TYPE_PERSON)
})public abstract class RsAktoer {

    @ApiModelProperty(value = "orgnummer/fnr")
    public abstract String getArbeidsgiverId();

    public abstract RsKontaktinformasjon getKontaktinformasjon();

    @ApiModelProperty(required = true, value = TYPE_ORGANISASJON + "/" + TYPE_PERSON)
    private String aktoertype;
}
