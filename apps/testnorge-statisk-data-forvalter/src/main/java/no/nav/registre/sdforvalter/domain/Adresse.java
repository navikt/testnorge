package no.nav.registre.sdforvalter.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.sdforvalter.database.model.AdresseModel;
import no.nav.registre.testnorge.libs.dto.statiskedataforvalter.v1.AdresseDTO;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
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

    public Adresse(AdresseModel model) {
        this.adresse = model.getAdresse();
        this.postnr = model.getPostnr();
        this.kommunenr = model.getKommunenr();
        this.landkode = model.getLandkode();
        this.poststed = model.getPoststed();
    }


    public AdresseDTO toDTO() {
        return AdresseDTO
                .builder()
                .adresselinje1(adresse)
                .postnr(postnr)
                .kommunenr(kommunenr)
                .landkode(landkode)
                .poststed(poststed)
                .build();
    }

    public Adresse(AdresseDTO dto) {
        adresse = dto.getAdresselinje1();
        postnr = dto.getPostnr();
        kommunenr = dto.getKommunenr();
        landkode = dto.getLandkode();
        poststed = dto.getPoststed();
    }

}