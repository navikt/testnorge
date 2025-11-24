package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Soek;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SoekRepository extends ReactiveSortingRepository<Soek, Long> {

    Flux<Soek> findByBrukerIdAndSoekTypeOrderByIdDesc(Long brukerId, Soek.SoekType soekType);

    Mono<Boolean> existsByBrukerIdAndSoekTypeAndSoekVerdi(Long brukerId, Soek.SoekType soekType, String soekVerdi);

    @Modifying
    Mono<Void> delete(Soek soek);

    Mono<Soek> save(Soek soek);
}