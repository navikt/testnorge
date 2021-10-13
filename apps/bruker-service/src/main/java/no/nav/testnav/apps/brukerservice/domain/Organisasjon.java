package no.nav.testnav.apps.brukerservice.domain;

import no.nav.testnav.apps.brukerservice.client.dto.OrganisasjonDTO;

public class Organisasjon {
    private final String navn;
    private final String organisasjonsnummer;
    private final String organisasjonsform;

    public Organisasjon(OrganisasjonDTO dto) {
        this.navn = dto.navn();
        this.organisasjonsnummer = dto.orgnisasjonsnummer();
        this.organisasjonsform = dto.orgnisasjonsfrom();
    }

    public String getNavn() {
        return navn;
    }

    public String getOrganisasjonsnummer() {
        return organisasjonsnummer;
    }

    public String getOrganisasjonsform() {
        return organisasjonsform;
    }
}
