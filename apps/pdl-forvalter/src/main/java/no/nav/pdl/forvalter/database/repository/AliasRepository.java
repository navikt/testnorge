package no.nav.pdl.forvalter.database.repository;

import no.nav.pdl.forvalter.database.model.DbAlias;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

@Repository
public interface AliasRepository extends ReactiveCrudRepository<DbAlias, Long> {

    Mono<DbAlias> findByTidligereIdent(String ident);

    Flux<DbAlias> findByPersonId(Long personId);

    Flux<DbAlias> findByTidligereIdentIn(List<String> ident);

    Mono<Boolean> existsByTidligereIdent(String ident);

    Mono<Boolean> existsByPersonId(Long personId);

    @Modifying
    @Query("""
           delete from alias a
           where a.person_id in (select id from person p where p.ident in (:ident))
           """)
    Mono<Void> deleteByIdentIn(Collection<String> ident);
}
