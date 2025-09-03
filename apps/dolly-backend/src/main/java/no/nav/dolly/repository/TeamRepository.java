package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Team;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface TeamRepository extends ReactiveCrudRepository<Team, Long> {

    Mono<Team> findByNavn(String navn);
}