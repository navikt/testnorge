package no.nav.registre.testnorge.organisasjonfastedataservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import no.nav.registre.testnorge.libs.dto.organiasjonbestilling.v1.ItemDTO;
import no.nav.registre.testnorge.organisasjonfastedataservice.consumer.OrganisasjonBestillingConsumer;
import no.nav.registre.testnorge.organisasjonfastedataservice.consumer.OrganisasjonMottakConsumer;
import no.nav.registre.testnorge.organisasjonfastedataservice.domain.Organisasjon;

@Service
@RequiredArgsConstructor
public class OrganisasjonOrdreService {
    private final OrganisasjonMottakConsumer organisasjonMottakConsumer;
    private final OrganisasjonBestillingConsumer organisasjonBestillingConsumer;

    public String create(Organisasjon organisasjon, String miljo) {
        return organisasjonMottakConsumer.create(organisasjon, miljo);
    }

    public String change(Organisasjon organisasjon, String miljo) {
        return organisasjonMottakConsumer.change(organisasjon, miljo);
    }

    public List<ItemDTO> getStatus(String ordreId) {
        return organisasjonBestillingConsumer.getOrdreStatus(ordreId);
    }

}
