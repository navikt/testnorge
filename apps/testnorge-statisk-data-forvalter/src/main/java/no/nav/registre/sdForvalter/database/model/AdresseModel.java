package no.nav.registre.sdForvalter.database.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Embeddable;

import no.nav.registre.sdForvalter.domain.Adresse;

@ToString
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class AdresseModel {
    private String adresse;
    private String postnr;
    private String kommunenr;
    private String landkode;
    private String poststed;

    public AdresseModel(Adresse adresse) {
        this.adresse = adresse.getAdresse();
        this.postnr = adresse.getPostnr();
        this.kommunenr = adresse.getKommunenr();
        this.landkode = adresse.getLandkode();
        this.poststed = adresse.getPoststed();
    }
}
