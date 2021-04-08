package no.nav.registre.testnorge.organisasjonfastedataservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dto.organisasjonfastedataservice.v1.Gruppe;
import no.nav.registre.testnorge.libs.dto.organisasjonfastedataservice.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.organisasjonfastedataservice.repository.model.OrganisasjonModel;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Organisasjon {

    public Organisasjon(OrganisasjonDTO dto) {
        this.orgnummer = dto.getOrgnummer();
        this.enhetstype = dto.getEnhetstype();
        this.navn = dto.getNavn();
        this.redigertNavn = dto.getRedigertNavn();
        this.epost = dto.getEpost();
        this.internetAdresse = dto.getInternetAdresse();
        this.overenhet = dto.getOverenhet();
        this.forretningsAdresse = dto.getForretningsAdresse() != null ? new Adresse(dto.getForretningsAdresse()) : null;
        this.postadresse = dto.getPostadresse() != null ? new Adresse(dto.getPostadresse()) : null;
        this.opprinnelse = dto.getOpprinnelse();
        this.tags = dto.getTags();
        this.underenheter = new ArrayList<>();
    }

    String orgnummer;
    String enhetstype;
    String navn;
    String redigertNavn;
    String epost;
    String internetAdresse;
    String overenhet;
    Adresse forretningsAdresse;
    Adresse postadresse;
    String opprinnelse;
    Set<String> tags;
    List<Organisasjon> underenheter;

    public Organisasjon(OrganisasjonModel model) {
        var organisasjon = model.getOrganisasjon();
        orgnummer = model.getOrgnummer();
        enhetstype = organisasjon.getEnhetstype();
        navn = organisasjon.getNavn();
        redigertNavn = organisasjon.getRedigertNavn();
        epost = organisasjon.getEpost();
        internetAdresse = organisasjon.getInternetAdresse();
        overenhet = organisasjon.getOverenhet();
        forretningsAdresse = organisasjon.getForretningsAdresse();
        postadresse = organisasjon.getPostadresse();
        opprinnelse = organisasjon.getOpprinnelse();
        tags = organisasjon.getTags();
        underenheter = Optional
                .ofNullable(model.getUnderenheter())
                .map(value -> value.stream().map(Organisasjon::new).collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }


    @JsonIgnore
    public List<Organisasjon> getUnderenheter() {
        return underenheter;
    }

    public OrganisasjonDTO toDTO() {
        return OrganisasjonDTO
                .builder()
                .orgnummer(orgnummer)
                .enhetstype(enhetstype)
                .navn(navn)
                .redigertNavn(redigertNavn)
                .epost(epost)
                .internetAdresse(internetAdresse)
                .overenhet(overenhet)
                .forretningsAdresse(forretningsAdresse != null ? forretningsAdresse.toDTO() : null)
                .postadresse(postadresse != null ? postadresse.toDTO() : null)
                .opprinnelse(opprinnelse)
                .tags(tags)
                .build();
    }

    public OrganisasjonModel toModel(Gruppe gruppe) {
        return OrganisasjonModel
                .builder()
                .organisasjon(this)
                .orgnummer(orgnummer)
                .gruppe(gruppe)
                .overenhet(overenhet)
                .build();
    }

}
