package no.nav.registre.testnorge.organisasjon.service;

import lombok.RequiredArgsConstructor;

import no.nav.registre.testnorge.organisasjon.consumer.EregConsumer;
import no.nav.registre.testnorge.organisasjon.consumer.EregMapperConsumer;
import no.nav.registre.testnorge.organisasjon.domain.Organisasjon;

@RequiredArgsConstructor
public class OrganisasjonService {
    private final EregMapperConsumer eregMapperConsumer;
    private final EregConsumer consumer;

    public Organisasjon getOrganisasjon(String orgnummer, String miljo) {
        return consumer.getOrganisasjon(orgnummer, miljo);
    }

    public void putOrganisasjon(Organisasjon organisasjon, String miljo) {
        var response = getOrganisasjon(organisasjon.getOrgnummer(), miljo);
        if (response == null) {
            eregMapperConsumer.createOrganisasjon(organisasjon, miljo);
        } else {
            eregMapperConsumer.updateOrgansiasjon(organisasjon, miljo);
        }
    }
}
