package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.RsDollyStatistikk;
import no.nav.dolly.repository.BestillingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DollyStatistikkService {

    private final BestillingRepository bestillingRepository;

    @Transactional
    public RsDollyStatistikk getDollyStatistikk(String brukerId) {

        return new RsDollyStatistikk(
                bestillingRepository.countAllByBruker_BrukerIdEquals(brukerId),
                bestillingRepository.streamAllByBruker_BrukerIdEquals(brukerId)
                        .map(Bestilling::getAntallIdenter).mapToLong(Integer::longValue).sum()
        );
    }
}
