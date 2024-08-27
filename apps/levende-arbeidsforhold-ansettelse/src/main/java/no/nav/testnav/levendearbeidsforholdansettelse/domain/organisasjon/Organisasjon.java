package no.nav.testnav.levendearbeidsforholdansettelse.domain.organisasjon;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.levendearbeidsforholdansettelse.domain.dto.OrganisasjonOldDTO;


import java.util.Collections;
import java.util.List;


@Slf4j
@Value
public class Organisasjon {
    String orgnummer;
    String enhetType;
    String navn;
    String juridiskEnhet;
    Adresse postadresse;
    Adresse forretningsadresser;
    String redigertnavn;
    List<String> driverVirksomheter;

    public Organisasjon(OrganisasjonOldDTO dto) {

        navn = dto.getNavn().getNavnelinje1();
        orgnummer = dto.getOrganisasjonsnummer();
        juridiskEnhet = dto.getParents().isEmpty() ? null : dto.getParents().get(0).getOrganisasjonsnummer();
        redigertnavn = dto.getNavn().getRedigertnavn();
        enhetType = dto.getDetaljer().getEnhetstype();

        driverVirksomheter = dto.getChildren() != null
                ? dto.getChildren().stream().map(OrganisasjonOldDTO::getOrganisasjonsnummer).toList()
                : Collections.emptyList();

        if (dto.getOrganisasjonDetaljer() != null) {
            var postadresser = dto.getOrganisasjonDetaljer().getPostadresser();
            if (postadresser != null && !postadresser.isEmpty()) {
                this.postadresse = new Adresse(postadresser.get(0));
            } else {
                this.postadresse = null;
            }
            var forretningsadresser = dto.getOrganisasjonDetaljer().getForretningsadresser();
            if (forretningsadresser != null && !forretningsadresser.isEmpty()) {
                this.forretningsadresser = new Adresse(forretningsadresser.get(0));
            } else {
                this.forretningsadresser = null;
            }
        } else {
            this.postadresse = null;
            this.forretningsadresser = null;
        }
    }

    public no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO toDTO() {
        return no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO.builder().navn(navn).enhetType(enhetType).orgnummer(orgnummer).juridiskEnhet(juridiskEnhet).postadresse(postadresse != null ? postadresse.toDTO() : null).forretningsadresser(forretningsadresser != null ? forretningsadresser.toDTO() : null).redigertnavn(redigertnavn).driverVirksomheter(driverVirksomheter).build();
    }
}
