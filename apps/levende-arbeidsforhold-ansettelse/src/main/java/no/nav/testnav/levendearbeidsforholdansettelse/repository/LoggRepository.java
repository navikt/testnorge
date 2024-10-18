package no.nav.testnav.levendearbeidsforholdansettelse.repository;

import no.nav.testnav.levendearbeidsforholdansettelse.entity.AnsettelseLogg;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LoggRepository extends ReactiveSortingRepository<AnsettelseLogg, Long> {

    Flux<AnsettelseLogg> findAllBy(Pageable pageable);

    Mono<Long> countAllBy();
}
