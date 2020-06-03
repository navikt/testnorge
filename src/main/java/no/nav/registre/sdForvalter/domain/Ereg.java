package no.nav.registre.sdForvalter.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;
import org.apache.logging.log4j.util.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import no.nav.registre.sdForvalter.consumer.rs.request.ereg.EregMapperRequest;
import no.nav.registre.sdForvalter.database.model.EregModel;
import no.nav.registre.sdForvalter.dto.organisasjon.v1.OrganisasjonDTO;


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
    public boolean isKanHaArbeidsforhold() {
        return Strings.isNotBlank(enhetstype) && (enhetstype.equals("BEDR") || enhetstype.equals("AAFY"));
    }

    @Builder
    public Ereg(String gruppe, String opprinnelse, String orgnr, String enhetstype, String navn, String epost, String internetAdresse, String naeringskode, String juridiskEnhet, Adresse forretningsAdresse, Adresse postadresse) {
        super(gruppe, opprinnelse);
        this.orgnr = orgnr;
        this.enhetstype = enhetstype;
        this.navn = navn;
        this.epost = epost;
        this.internetAdresse = internetAdresse;
        this.naeringskode = naeringskode;
        this.juridiskEnhet = juridiskEnhet;
        this.forretningsAdresse = forretningsAdresse;
        this.postadresse = postadresse;
    }

    public Ereg(EregModel model) {
        super(model);
        orgnr = model.getOrgnr();
        enhetstype = model.getEnhetstype();
        navn = model.getNavn();
        epost = model.getEpost();
        internetAdresse = model.getInternetAdresse();
        naeringskode = model.getNaeringskode();
        juridiskEnhet = model.getParent() != null ? model.getParent().getOrgnr() : null;
        forretningsAdresse = model.getForretningsAdresse() != null ? new Adresse(model.getForretningsAdresse()) : null;
        postadresse = model.getPostadresse() != null ? new Adresse(model.getPostadresse()) : null;
    }

    public Ereg(OrganisasjonDTO dto) {
        super(dto.getGruppe(), dto.getOpprinnelse());
        orgnr = dto.getOrgnr();
        enhetstype = dto.getEnhetstype();
        navn = dto.getNavn();
        epost = dto.getEpost();
        internetAdresse = dto.getInternetAdresse();
        naeringskode = dto.getNaeringskode();
        juridiskEnhet = dto.getJuridiskEnhet();
        forretningsAdresse = new Adresse(dto.getForretningsAdresse());
        postadresse = new Adresse(dto.getPostadresse());
    }

    public Ereg(EregMapperRequest eregMapperRequest) {
        super("WIP", "Brreg");

        orgnr = eregMapperRequest.getOrgnr();
        enhetstype = eregMapperRequest.getEnhetstype();
        if (eregMapperRequest.getNavn() != null && eregMapperRequest.getNavn().getNavneListe() != null) {
            navn = String.join(" ", eregMapperRequest.getNavn().getNavneListe());
        } else {
            navn = null;
        }
        epost = eregMapperRequest.getEpost();
        internetAdresse = eregMapperRequest.getInternetAdresse();

        forretningsAdresse = eregMapperRequest.getForretningsAdresse() != null
                ? new Adresse(eregMapperRequest.getForretningsAdresse())
                : null;
        postadresse = eregMapperRequest.getAdresse() != null ? new Adresse(eregMapperRequest.getAdresse()) : null;

        naeringskode = null;

        List<Map<String, String>> eregMapperRequestKnytninger = eregMapperRequest.getKnytninger();
        if (eregMapperRequestKnytninger != null) {
            juridiskEnhet = eregMapperRequestKnytninger
                    .stream()
                    .map(stringStringMap -> stringStringMap.get("orgnr"))
                    .findFirst()
                    .orElse(null);
        } else {
            juridiskEnhet = null;
        }
    }

    public OrganisasjonDTO toDTO() {
        return OrganisasjonDTO
                .builder()
                .orgnr(orgnr)
                .enhetstype(enhetstype)
                .navn(navn)
                .epost(epost)
                .forretningsAdresse(forretningsAdresse != null ? forretningsAdresse.toDTO() : null)
                .postadresse(postadresse != null ? postadresse.toDTO() : null)
                .internetAdresse(internetAdresse)
                .juridiskEnhet(juridiskEnhet)
                .naeringskode(naeringskode)
                .kanHaArbeidsforhold(isKanHaArbeidsforhold())
                .build();
    }
}