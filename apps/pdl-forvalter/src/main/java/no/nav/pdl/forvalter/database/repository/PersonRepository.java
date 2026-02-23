package no.nav.pdl.forvalter.database.repository;

import no.nav.pdl.forvalter.database.model.DbPerson;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface PersonRepository extends ReactiveCrudRepository<DbPerson, Long> {

    Mono<DbPerson> findByIdent(String ident);

    Flux<DbPerson> findByIdentIn(Collection<String> identer, Pageable pageable);

    Flux<DbPerson> findByIdIn(List<Long> identer);

    @Modifying
    Mono<Void> deleteByIdentIn(Set<String> ident);

    @Modifying
    Mono<Void> deleteByIdent(String ident);

    Mono<Boolean> existsByIdent(String ident);

    @Query("""
            select * from person p
            where (:partialIdent is null or :partialIdent is not null and p.ident like %:partialIdent%)
            and (:partialNavn1 is null or :partialNavn1 is not null and (upper(p.etternavn) like %:partialNavn1% or upper(p.fornavn) like %:partialNavn1%))
            and (:partialNavn2 is null or :partialNavn2 is not null and (upper(p.etternavn) like %:partialNavn2% or upper(p.fornavn) like %:partialNavn2%))
            and (not exists (from DbAlias a where a.tidligereIdent = p.ident) or :partialNavn1 is null and :partialNavn2 is null)
            """)
    Flux<DbPerson> findByWildcardIdent(@Param("partialIdent") String partialIdent,
                                       @Param("partialNavn1") String partialNavn1,
                                       @Param("partialNavn2") String partialNavn2,
                                       Pageable pageable);
}
