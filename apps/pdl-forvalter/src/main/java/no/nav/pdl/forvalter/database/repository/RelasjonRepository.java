package no.nav.pdl.forvalter.database.repository;

import lombok.Data;
import no.nav.pdl.forvalter.database.model.DbRelasjon;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RelasjonRepository extends ReactiveCrudRepository<DbRelasjon, Long> {

    Flux<DbRelasjon> findByPersonId(Long personId);

    Mono<Boolean> existsByPersonId(Long personId);
}
