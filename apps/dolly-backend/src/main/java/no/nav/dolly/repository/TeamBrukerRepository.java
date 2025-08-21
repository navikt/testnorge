package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.TeamBruker;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface TeamBrukerRepository extends ReactiveCrudRepository<TeamBruker, Long> {

    @Modifying
    Mono<Void> deleteByTeamId(Long teamId);

    Mono<Boolean> existsByTeamIdAndBrukerId(Long teamId, Long brukerId);

    Flux<TeamBruker> findAllByBrukerId(Long brukerId);

    Mono<TeamBruker> findByTeamIdAndBrukerId(Long teamId, Long brukerId);
}