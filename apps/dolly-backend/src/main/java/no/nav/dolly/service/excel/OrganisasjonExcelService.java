package no.nav.dolly.service.excel;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.organisasjonforvalter.OrganisasjonConsumer;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingProgress;
import no.nav.dolly.repository.OrganisasjonBestillingRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class OrganisasjonExcelService {

    private static final int FETCH_BLOCK_SIZE = 10;

    private final OrganisasjonBestillingRepository organisasjonBestillingRepository;
    private final OrganisasjonConsumer organisasjonConsumer;

    public void prepareOrganisasjonSheet(Bruker bruker) {

        var organisasjoner = organisasjonBestillingRepository.findByBruker(bruker).stream()
                .map(OrganisasjonBestilling::getProgresser)
                .flatMap(Collection::stream)
                .map(OrganisasjonBestillingProgress::getOrganisasjonsnummer)
                .toList();

        Flux.range(0, organisasjoner.size()/FETCH_BLOCK_SIZE + 1)
                        .flatMap(index -> organisasjonConsumer.hentOrganisasjon(
                                organisasjoner.subList(index * FETCH_BLOCK_SIZE),
                                Math.min((index + 1) * FETCH_BLOCK_SIZE, organisasjoner.size()

    }
}
