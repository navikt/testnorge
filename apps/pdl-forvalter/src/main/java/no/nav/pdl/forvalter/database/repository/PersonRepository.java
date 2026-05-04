package no.nav.pdl.forvalter.database.repository;

import no.nav.pdl.forvalter.database.model.DbPerson;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface PersonRepository extends ReactiveSortingRepository<DbPerson, Long> {

    Mono<DbPerson> findByIdent(String ident);
    Flux<DbPerson> findByIdentIn(List<String> ident);

    Mono<DbPerson> save(DbPerson person);
    Flux<DbPerson> saveAll(Collection<DbPerson> persons);

    Flux<DbPerson> findByIdentInOrderBySistOppdatertDesc(Collection<String> idents);

    Mono<DbPerson> findById(Long id);
    Flux<DbPerson> findByIdIn(Set<Long> ids);

    @Modifying
    Mono<Void> deleteByIdentIn(Set<String> ident);

    @Modifying
    Mono<Void> deleteByIdent(String ident);

    Mono<Boolean> existsByIdent(String ident);

    @Query("""
            select * from person p
            where (:partialIdent is null or :partialIdent is not null and p.ident like concat('%', :partialIdent, '%'))
            and (:partialNavn1 is null or :partialNavn1 is not null and (upper(p.etternavn) like concat('%', :partialNavn1, '%') or upper(p.fornavn) like concat('%', :partialNavn1,'%')))
            and (:partialNavn2 is null or :partialNavn2 is not null and (upper(p.etternavn) like concat('%', :partialNavn2, '%') or upper(p.fornavn) like concat('%', :partialNavn2,'%')))
            and (not exists (select * from alias a where a.tidligere_ident = p.ident) or :partialNavn1 is null and :partialNavn2 is null)
            """)
    Flux<DbPerson> findByWildcardIdent(@Param("partialIdent") String partialIdent,
                                       @Param("partialNavn1") String partialNavn1,
                                       @Param("partialNavn2") String partialNavn2,
                                       Pageable pageable);
}
