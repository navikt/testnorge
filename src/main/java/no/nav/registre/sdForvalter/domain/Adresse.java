package no.nav.registre.sdForvalter.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.sdForvalter.database.model.AdresseModel;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Adresse {
    @JsonProperty
    private final String adresse;
    @JsonProperty
    private final String postnr;
    @JsonProperty
    private final String kommunenr;
    @JsonProperty
    private final String landkode;
    @JsonProperty
    private final String poststed;

    public Adresse(AdresseModel model){
        this.adresse = model.getAdresse();
        this.postnr = model.getPostnr();
        this.kommunenr = model.getKommunenr();
        this.landkode = model.getLandkode();
        this.poststed = model.getPoststed();
    }
}