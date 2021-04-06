package no.nav.registre.testnorge.organisasjonservice.domain;

import java.util.Arrays;

import no.nav.registre.testnorge.organisasjonservice.consumer.dto.AdresseDTO;

public class Adresse {
    private final String kommunenummer;
    private final String adresselinje1;
    private final String adresselinje2;
    private final String adresselinje3;
    private final String landkode;
    private final String postnummer;
    private final String poststed;

    public Adresse(AdresseDTO dto) {
        this.kommunenummer = dto.getKommunenummer();
        this.adresselinje1 = dto.getAdresselinje1();
        this.adresselinje2 = dto.getAdresselinje2();
        this.adresselinje3 = dto.getAdresselinje3();
        this.landkode = dto.getLandkode();
        this.postnummer = dto.getPostnummer();
        this.poststed = dto.getPoststed();
    }

    public Adresse(no.nav.registre.testnorge.libs.dto.organisasjon.v1.AdresseDTO dto) {
        this.kommunenummer = dto.getKommunenummer();
        this.adresselinje1 = dto.getAdresselinje1();
        this.adresselinje2 = dto.getAdresselinje2();
        this.adresselinje3 = dto.getAdresselinje3();
        this.landkode = dto.getLandkode();
        this.postnummer = dto.getPostnummer();
        this.poststed = dto.getPoststed();
    }

    public no.nav.registre.testnorge.libs.dto.organisasjon.v1.AdresseDTO toDTO() {
        return no.nav.registre.testnorge.libs.dto.organisasjon.v1.AdresseDTO.builder()
                .kommunenummer(kommunenummer)
                .adresselinje1(adresselinje1)
                .adresselinje2(adresselinje2)
                .adresselinje3(adresselinje3)
                .landkode(landkode)
                .postnummer(postnummer)
                .poststed(poststed)
                .build();
    }


    public no.nav.registre.testnorge.libs.dto.eregmapper.v1.AdresseDTO toAdresseDTO() {
        return no.nav.registre.testnorge.libs.dto.eregmapper.v1.AdresseDTO
                .builder()
                .adresser(Arrays.asList(
                        adresselinje1,
                        adresselinje2,
                        adresselinje3
                ))
                .kommunenr(kommunenummer)
                .landkode(landkode)
                .postnr(postnummer)
                .poststed(poststed)
                .build();
    }

}
