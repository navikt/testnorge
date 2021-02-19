package no.nav.registre.testnorge.organisasjon.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import no.nav.registre.testnorge.organisasjon.consumer.EregConsumer;
import no.nav.registre.testnorge.organisasjon.domain.Organisasjon;

@Service
@RequiredArgsConstructor
public class OrganisasjonService {
    private final EregConsumer consumer;

    public Organisasjon getOrganisasjon(String orgnummer, String miljo) {
        return consumer.getOrganisasjon(orgnummer, miljo);
    }
}
