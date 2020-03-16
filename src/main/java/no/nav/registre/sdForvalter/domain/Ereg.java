package no.nav.registre.sdForvalter.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

import no.nav.registre.sdForvalter.database.model.EregModel;


@Value
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Ereg extends FasteData {
    @JsonProperty(required = true)
    private final String orgnr;
    @JsonProperty(required = true)
    private final String enhetstype;
    @JsonProperty
    private final String navn;
    @JsonProperty
    private final String epost;
    @JsonProperty
    private final String internetAdresse;
    @JsonProperty
    private final String naeringskode;
    @JsonProperty
    private final String juridiskEnhet;
    @JsonProperty
    private final Adresse forretningsAdresse;
    @JsonProperty
    private final Adresse postadresse;

    public Ereg(EregModel model) {
        super(model);
        orgnr = model.getOrgnr();
        enhetstype = model.getEnhetstype();
        navn = model.getNavn();
        epost = model.getEpost();
        internetAdresse = model.getInternetAdresse();
        naeringskode = model.getNaeringskode();
        juridiskEnhet = model.getParent() != null ? model.getParent().getOrgnr() : null;
        forretningsAdresse = model.getForretningsAdresse() != null ? new Adresse(model.getForretningsAdresse()) : null;
        postadresse = model.getPostadresse() != null ? new Adresse(model.getPostadresse()) : null;
    }
}