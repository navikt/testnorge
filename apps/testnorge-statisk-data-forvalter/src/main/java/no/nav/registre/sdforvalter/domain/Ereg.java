package no.nav.registre.sdforvalter.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;
import org.apache.logging.log4j.util.Strings;
import org.apache.logging.log4j.util.Supplier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.sdforvalter.database.model.EregModel;
import no.nav.registre.sdforvalter.database.model.TagModel;
import no.nav.testnav.libs.dto.statiskedataforvalter.v1.OrganisasjonDTO;


@Value
@NoArgsConstructor(force = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Ereg extends FasteData {
    @JsonProperty(required = true)
    private final String orgnr;
    @JsonProperty(required = true)
    private final String enhetstype;
    @JsonProperty
    private final String navn;
    @JsonProperty
    private final String redigertNavn;
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
    private final Set<String> tags;

    @JsonProperty
    public boolean isKanHaArbeidsforhold() {
        return Strings.isNotBlank(enhetstype) && (enhetstype.equals("BEDR") || enhetstype.equals("AAFY"));
    }

    @Builder
    public Ereg(String gruppe, String opprinnelse, String orgnr, String enhetstype, String navn, String redigertNavn, String epost, String internetAdresse, String naeringskode, String juridiskEnhet, Adresse forretningsAdresse, Adresse postadresse, Set<String> tags) {
        super(gruppe, opprinnelse);
        this.orgnr = orgnr;
        this.enhetstype = enhetstype;
        this.navn = navn;
        this.redigertNavn = redigertNavn;
        this.epost = epost;
        this.internetAdresse = internetAdresse;
        this.naeringskode = naeringskode;
        this.juridiskEnhet = juridiskEnhet;
        this.forretningsAdresse = forretningsAdresse;
        this.postadresse = postadresse;
        this.tags = tags;
    }

    public Ereg(EregModel model, List<TagModel> tagModels) {
        super(model);
        orgnr = model.getOrgnr();
        enhetstype = model.getEnhetstype();
        navn = model.getNavn();
        redigertNavn = model.getRedigertNavn();
        epost = model.getEpost();
        internetAdresse = model.getInternetAdresse();
        naeringskode = model.getNaeringskode();
        juridiskEnhet = model.getParent() != null ? model.getParent().getOrgnr() : null;
        forretningsAdresse = model.getForretningsAdresse() != null ? new Adresse(model.getForretningsAdresse()) : null;
        postadresse = model.getPostadresse() != null ? new Adresse(model.getPostadresse()) : null;
        tags = tagModels.stream().map(TagModel::getTag).collect(Collectors.toSet());
    }

    public Ereg(OrganisasjonDTO dto) {
        super(dto.getGruppe(), dto.getOpprinnelse());
        orgnr = dto.getOrgnr();
        enhetstype = dto.getEnhetstype();
        navn = dto.getNavn();
        redigertNavn = dto.getRedigertNavn();
        epost = dto.getEpost();
        internetAdresse = dto.getInternetAdresse();
        naeringskode = dto.getNaeringskode();
        juridiskEnhet = dto.getJuridiskEnhet();
        forretningsAdresse = dto.getForretningsAdresse() != null ? new Adresse(dto.getForretningsAdresse()) : null;
        postadresse = dto.getPostadresse() != null ? new Adresse(dto.getPostadresse()) : null;
        tags = new HashSet<>();
    }

    public OrganisasjonDTO toDTO() {
        return OrganisasjonDTO
                .builder()
                .orgnr(orgnr)
                .enhetstype(enhetstype)
                .navn(navn)
                .redigertNavn(redigertNavn)
                .epost(epost)
                .forretningsAdresse(forretningsAdresse != null ? forretningsAdresse.toDTO() : null)
                .postadresse(postadresse != null ? postadresse.toDTO() : null)
                .internetAdresse(internetAdresse)
                .juridiskEnhet(juridiskEnhet)
                .naeringskode(naeringskode)
                .kanHaArbeidsforhold(isKanHaArbeidsforhold())
                .build();
    }

    public no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.OrganisasjonDTO toDTOv2(Supplier<String> generateDefaultName) {
        return no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.OrganisasjonDTO
                .builder()
                .orgnummer(orgnr)
                .enhetstype(enhetstype)
                .navn(this.navn != null ? this.navn : generateDefaultName.get())
                .redigertNavn(redigertNavn)
                .epost(epost)
                .forretningsAdresse(forretningsAdresse != null ? forretningsAdresse.toDTOv2() : null)
                .postadresse(postadresse != null ? postadresse.toDTOv2() : null)
                .internetAdresse(internetAdresse)
                .overenhet(juridiskEnhet)
                .naeringskode(naeringskode)
                .opprinnelse(getOpprinnelse())
                .tags(tags)
                .build();
    }
}