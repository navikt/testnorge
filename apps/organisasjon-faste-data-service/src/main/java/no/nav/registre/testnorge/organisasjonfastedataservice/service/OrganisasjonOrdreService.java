package no.nav.registre.testnorge.organisasjonfastedataservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import no.nav.testnav.libs.dto.organisajonbestilling.v1.ItemDTO;
import no.nav.testnav.libs.dto.organisasjonfastedataservice.v1.Gruppe;
import no.nav.registre.testnorge.organisasjonfastedataservice.consumer.OrganisasjonBestillingConsumer;
import no.nav.registre.testnorge.organisasjonfastedataservice.consumer.OrganisasjonMottakConsumer;
import no.nav.registre.testnorge.organisasjonfastedataservice.domain.Organisasjon;
import no.nav.registre.testnorge.organisasjonfastedataservice.repository.OrganisasjonRepository;

@Service
@RequiredArgsConstructor
public class OrganisasjonOrdreService {
    private final OrganisasjonMottakConsumer organisasjonMottakConsumer;
    private final OrganisasjonBestillingConsumer organisasjonBestillingConsumer;
    private final OrganisasjonRepository repository;

    public String create(Organisasjon organisasjon, String miljo) {
        var ordreId = UUID.randomUUID().toString();
        return organisasjonMottakConsumer.create(organisasjon, miljo, ordreId);
    }

    public String change(Organisasjon organisasjon, String miljo) {
        var ordreId = UUID.randomUUID().toString();
        return organisasjonMottakConsumer.change(organisasjon, miljo, ordreId);
    }

    public List<ItemDTO> getStatus(String ordreId) {
        return organisasjonBestillingConsumer.getOrdreStatus(ordreId);
    }

    public String create(Gruppe gruppe, String miljo) {
        var ordreId = UUID.randomUUID().toString();
        repository.findAllByGruppeAndOverenhetIsNull(gruppe)
                .stream()
                .map(Organisasjon::new)
                .forEach(organisasjon -> organisasjonMottakConsumer.create(organisasjon, miljo, ordreId));
        return ordreId;
    }

    public String change(Gruppe gruppe, String miljo) {
        var ordreId = UUID.randomUUID().toString();
        repository.findAllByGruppeAndOverenhetIsNull(gruppe)
                .stream()
                .map(Organisasjon::new)
                .forEach(organisasjon -> organisasjonMottakConsumer.change(organisasjon, miljo, ordreId));
        return ordreId;
    }

}
