package no.nav.organisasjonforvalter.service;

import lombok.RequiredArgsConstructor;
import no.nav.organisasjonforvalter.consumer.OrganisasjonMottakConsumer;
import no.nav.organisasjonforvalter.consumer.OrganisasjonNavnConsumer;
import no.nav.organisasjonforvalter.consumer.OrganisasjonNummerConsumer;
import no.nav.organisasjonforvalter.jpa.repository.AdresseRepository;
import no.nav.organisasjonforvalter.jpa.repository.OrganisasjonRepository;
import no.nav.organisasjonforvalter.provider.rs.requests.BestillingRequest;
import no.nav.organisasjonforvalter.provider.rs.responses.BestillingResponse;
import no.nav.registre.testnorge.libs.avro.organisasjon.Metadata;
import no.nav.registre.testnorge.libs.avro.organisasjon.Organisasjon;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BestillingService {

    private final OrganisasjonNavnConsumer organisasjonNavnConsumer;
    private final OrganisasjonNummerConsumer organisasjonNummerConsumer;
    private final OrganisasjonRepository organisasjonRepository;
    private final AdresseRepository adresseRepository;

    public BestillingResponse execute(BestillingRequest request) {

        List<String> orgname = organisasjonNavnConsumer.getOrgName(1);
        List<String> orgNummer = organisasjonNummerConsumer.getOrgnummer(1);


        return null;
    }
}
