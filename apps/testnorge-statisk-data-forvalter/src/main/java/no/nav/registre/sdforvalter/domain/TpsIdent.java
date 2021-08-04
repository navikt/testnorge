package no.nav.registre.sdforvalter.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.sdforvalter.database.model.TagModel;
import no.nav.registre.sdforvalter.database.model.TpsIdentModel;
import no.nav.testnav.libs.dto.person.v1.AdresseDTO;
import no.nav.testnav.libs.dto.person.v1.PersonDTO;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
public class TpsIdent extends FasteData {

    @JsonProperty
    private final String fnr;
    @JsonProperty("fornavn")
    private final String firstName;
    @JsonProperty("etternavn")
    private final String lastName;
    @JsonProperty("adresse")
    private final String address;
    @JsonProperty("postnr")
    private final String postNr;
    @JsonProperty("by")
    private final String city;
    @JsonProperty
    private final Set<String> tags;

    public TpsIdent(TpsIdentModel model, List<TagModel> tagModels) {
        super(model);
        fnr = model.getFnr();
        firstName = model.getFirstName();
        lastName = model.getLastName();
        address = model.getAddress();
        postNr = model.getPostNr();
        city = model.getCity();
        tags = tagModels.stream().map(TagModel::getTag).collect(Collectors.toSet());
    }

    @Builder
    public TpsIdent(String fnr, String firstName, String lastName, String address, String postNr, String city, String gruppe, String opprinnelse, Set<String> tags) {
        super(gruppe, opprinnelse);
        this.fnr = fnr;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.postNr = postNr;
        this.city = city;
        this.tags = tags;
    }

    public PersonDTO toDTO() {
        return PersonDTO.builder()
                .ident(fnr)
                .fornavn(firstName)
                .etternavn(lastName)
                .adresse(address != null ? new AdresseDTO(address, postNr, city, null) : null)
                .build();
    }


    public no.nav.testnav.libs.dto.personservice.v1.AdresseDTO toAdresseDTO(){
        return no.nav.testnav.libs.dto.personservice.v1.AdresseDTO
                .builder()
                .gatenavn(address)
                .postnummer(postNr)
                .poststed(city)
                .build();
    }

    public no.nav.testnav.libs.dto.personservice.v1.PersonDTO toDTOV2(){
        return no.nav.testnav.libs.dto.personservice.v1.PersonDTO
                .builder()
                .ident(fnr)
                .fornavn(firstName)
                .etternavn(lastName)
                .opprinnelse(getOpprinnelse())
                .tags(tags)
                .adresse(toAdresseDTO())
                .build();
    }

}
