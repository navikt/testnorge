package no.nav.registre.testnorge.organisasjonfastedataservice.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.testnorge.libs.dto.organisasjonfastedataservice.v1.AdresseDTO;


@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Adresse {
    public Adresse(AdresseDTO dto) {
        this.adresselinje1 = dto.getAdresselinje1();
        this.adresselinje2 = dto.getAdresselinje2();
        this.adresselinje3 = dto.getAdresselinje3();
        this.postnr = dto.getPostnr();
        this.kommunenr = dto.getKommunenr();
        this.landkode = dto.getLandkode();
        this.poststed = dto.getPoststed();
    }

    String adresselinje1;
    String adresselinje2;
    String adresselinje3;
    String postnr;
    String kommunenr;
    String landkode;
    String poststed;

    public AdresseDTO toDTO() {
        return AdresseDTO
                .builder()
                .adresselinje1(adresselinje1)
                .adresselinje2(adresselinje2)
                .adresselinje3(adresselinje3)
                .postnr(postnr)
                .kommunenr(kommunenr)
                .landkode(landkode)
                .poststed(poststed)
                .build();
    }

}
