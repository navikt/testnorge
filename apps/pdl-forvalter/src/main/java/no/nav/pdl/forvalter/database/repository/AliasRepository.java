package no.nav.pdl.forvalter.database.repository;

import no.nav.pdl.forvalter.database.model.DbAlias;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Repository
public interface AliasRepository extends ReactiveCrudRepository<DbAlias, Long> {

    Flux<DbAlias> findByPersonId(Long personId);

    Mono<DbAlias> findByTidligereIdent(String ident);

    Flux<DbAlias> findByTidligereIdentIn(List<String> ident);
}
