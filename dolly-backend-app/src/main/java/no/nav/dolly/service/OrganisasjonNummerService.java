package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.OrganisasjonNummer;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.OrganisasjonNummerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrganisasjonNummerService {

    private final OrganisasjonNummerRepository organisasjonProgressRepository;

    public Optional<OrganisasjonNummer> save(OrganisasjonNummer organisasjonNummer) {

        return organisasjonProgressRepository.save(organisasjonNummer);
    }

    public List<OrganisasjonNummer> fetchOrganisasjonNummereFromBestillingsId(Long bestillingsId) {
        return organisasjonProgressRepository.findByBestillingId(bestillingsId).orElseThrow(
                () -> new NotFoundException("Kunne ikke finne noen organisasjoner knyttet til bestillingId=" + bestillingsId + ", i tabell ORGANISASJON_BESTILLINGS_PROGRESS"));
    }
}