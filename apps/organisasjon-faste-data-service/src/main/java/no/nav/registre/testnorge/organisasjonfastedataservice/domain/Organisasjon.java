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
        orgnummer = dto.getOrgnummer();
        enhetstype = dto.getEnhetstype();
        navn = dto.getNavn();
        redigertNavn = dto.getRedigertNavn();
        epost = dto.getEpost();
        internetAdresse = dto.getInternetAdresse();
        overenhet = dto.getOverenhet();
        forretningsAdresse = dto.getForretningsAdresse() != null ? new Adresse(dto.getForretningsAdresse()) : null;
        postadresse = dto.getPostadresse() != null ? new Adresse(dto.getPostadresse()) : null;
        opprinnelse = dto.getOpprinnelse();
        tags = dto.getTags();
        model = null;
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
    @JsonIgnore
    OrganisasjonModel model;

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
        this.model = model;
    }

    @JsonIgnore
    public boolean kanHaArbidsforhold() {
        return enhetstype.equals("BEDR") || enhetstype.equals("AAFY");
    }


    @JsonIgnore
    public List<Organisasjon> getUnderenheter() {
        return Optional
                .ofNullable(model.getUnderenheter())
                .map(value -> value.stream().map(Organisasjon::new).collect(Collectors.toList()))
                .orElse(Collections.emptyList());
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
