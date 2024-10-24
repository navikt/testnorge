package no.nav.testnav.levendearbeidsforholdansettelse.repository;

import no.nav.testnav.levendearbeidsforholdansettelse.entity.AnsettelseLogg;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface LoggRepository extends R2dbcRepository<AnsettelseLogg, Long> {

    Flux<AnsettelseLogg> findAllBy(Pageable pageable);
    Mono<Long> countAllBy();

}
