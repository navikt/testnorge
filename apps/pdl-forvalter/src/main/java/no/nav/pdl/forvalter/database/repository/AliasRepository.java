package no.nav.pdl.forvalter.database.repository;

import no.nav.pdl.forvalter.database.model.DbAlias;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface AliasRepository extends ReactiveCrudRepository<DbAlias, Long> {

    Mono<DbAlias> findByTidligereIdent(String ident);

    Flux<DbAlias> findByTidligereIdentIn(List<String> ident);

    Mono<Boolean> existsByTidligereIdent(String ident);
}
