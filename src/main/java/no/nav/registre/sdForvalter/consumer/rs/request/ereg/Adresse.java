package no.nav.registre.sdForvalter.consumer.rs.request.ereg;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

import no.nav.registre.sdForvalter.database.model.AdresseModel;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Adresse {

    private List<String> adresser;
    private String postnr;
    private String kommunenr;
    private String landkode;
    private String poststed;


    public Adresse(no.nav.registre.sdForvalter.domain.Adresse adresse) {
        adresser = Collections.singletonList(adresse.getAdresse());
        kommunenr = adresse.getKommunenr();
        landkode = adresse.getLandkode();
        postnr = adresse.getPostnr();
        poststed = adresse.getPoststed();
    }
}
