package no.nav.testnav.apps.templatesearchservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.templatesearchservice.consumers.BrukerServiceConsumer;
import no.nav.testnav.apps.templatesearchservice.domain.TenorMalOwner;
import no.nav.testnav.apps.templatesearchservice.domain.TenorMalBrukerType;
import no.nav.testnav.apps.templatesearchservice.domain.TenorPersonMal;
import no.nav.testnav.apps.templatesearchservice.repository.TenorPersonMalRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.LinkedHashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TenorMalAccessService {

    private final TenorPersonMalRepository malRepository;
    private final BrukerServiceConsumer brukerServiceConsumer;

    public Flux<TenorPersonMal> getAccessibleMaler(TenorMalOwner currentUser) {

        return switch (currentUser.brukertype()) {
            case AZURE -> malRepository.findByBrukertype(TenorMalBrukerType.AZURE);
            case BANKID -> getAccessibleBankIdMaler(currentUser);
            case TEAM -> malRepository.findByBrukertypeAndBrukerIdIn(
                    TenorMalBrukerType.TEAM,
                    Set.of(currentUser.brukerId()));
        };
    }

    private Flux<TenorPersonMal> getAccessibleBankIdMaler(TenorMalOwner currentUser) {

        return brukerServiceConsumer.getKollegaerIOrganisasjon(currentUser.brukerId())
                .map(response -> {
                    var brukerIds = new LinkedHashSet<>(response.brukere());
                    brukerIds.add(currentUser.brukerId());
                    return brukerIds;
                })
                .flatMapMany(brukerIds -> malRepository.findByBrukertypeAndBrukerIdIn(
                        TenorMalBrukerType.BANKID,
                        brukerIds));
    }
}
