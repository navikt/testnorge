package no.nav.registre.sdForvalter.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.sdForvalter.database.model.EregModel;

@Value
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Ereg {
    @JsonProperty
    private final String orgnr;
    @JsonProperty
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
    @JsonProperty
    private final String gruppe;
    @JsonProperty
    private final String opprinelse;

    public Ereg(EregModel model) {
        this.orgnr = model.getOrgnr();
        this.enhetstype = model.getEnhetstype();
        this.navn = model.getNavn();
        this.epost = model.getEpost();
        this.internetAdresse = model.getInternetAdresse();
        this.naeringskode = model.getNaeringskode();
        this.juridiskEnhet = model.getParent() != null ? model.getParent().getOrgnr() : null;
        this.opprinelse = model.getKildeSystemModel() != null ? model.getKildeSystemModel().getNavn() : null;
        this.forretningsAdresse = model.getForretningsAdresse() != null ? new Adresse(model.getForretningsAdresse()) : null;
        this.postadresse = model.getPostadresse() != null ? new Adresse(model.getPostadresse()) : null;
        this.gruppe = model.getGruppeModel() != null ? model.getGruppeModel().getKode() : null;
    }
}