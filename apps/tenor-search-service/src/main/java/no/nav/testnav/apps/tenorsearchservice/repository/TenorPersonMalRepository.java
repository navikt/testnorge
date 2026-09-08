package no.nav.testnav.apps.tenorsearchservice.repository;

import no.nav.testnav.apps.tenorsearchservice.domain.TenorMalBrukerType;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorPersonMal;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Repository
public interface TenorPersonMalRepository extends ReactiveSortingRepository<TenorPersonMal, Long> {

    Mono<Void> deleteAll();

    Mono<TenorPersonMal> findByBrukerIdAndMalNavnIgnoreCase(
            String brukerId,
            String malNavn);

    Mono<TenorPersonMal> save(TenorPersonMal mal);

    Flux<TenorPersonMal> findByBrukertype(TenorMalBrukerType brukertype);

    Flux<TenorPersonMal> findByBrukertypeAndBrukerIdIn(
            TenorMalBrukerType brukertype,
            Collection<String> brukerIds);

    Mono<Long> deleteByIdAndBrukerId(Long id, String brukerId);

    Mono<TenorPersonMal> findByIdAndBrukerId(Long id, String brukerId);
}
