package no.nav.registre.testnorge.organisasjon.domain;

import no.nav.registre.testnorge.organisasjon.consumer.dto.OrganisasjonDTO;

public class Organisasjon {
    private final String orgnummer;
    private final String enhetType;
    private final String navn;
    private final String juridiskEnhet;
    private final Adresse postadresse;
    private final Adresse forretningsadresser;
    private final String redigertnavn;

    public Organisasjon(OrganisasjonDTO dto) {

        navn = dto.getNavn().getNavnelinje1();
        orgnummer = dto.getOrganisasjonsnummer();
        juridiskEnhet = dto.getParents().isEmpty() ? null : dto.getParents().get(0).getOrganisasjonsnummer();
        redigertnavn = dto.getNavn().getRedigertnavn();
        enhetType = dto.getDetaljer().getEnhetstype();

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

    public no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO toDTO(){
        return no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO.builder()
                .navn(navn)
                .enhetType(enhetType)
                .orgnummer(orgnummer)
                .juridiskEnhet(juridiskEnhet)
                .postadresse(postadresse != null ? postadresse.toDTO() : null)
                .forretningsadresser(forretningsadresser != null ? forretningsadresser.toDTO() : null)
                .redigertnavn(redigertnavn)
                .build();
    }
}
