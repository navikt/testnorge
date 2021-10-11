package no.nav.testnav.apps.brukerservice.domain;

import java.time.LocalDateTime;

import no.nav.testnav.apps.brukerservice.client.dto.OrganisasjonDTO;

public class Organisasjon {
    private final String navn;
    private final String orgnisasjonsnummer;
    private final String orgnisasjonsfrom;

    public Organisasjon(OrganisasjonDTO dto) {
        this.navn = dto.navn();
        this.orgnisasjonsnummer = dto.orgnisasjonsnummer();
        this.orgnisasjonsfrom = dto.orgnisasjonsfrom();
    }
}
