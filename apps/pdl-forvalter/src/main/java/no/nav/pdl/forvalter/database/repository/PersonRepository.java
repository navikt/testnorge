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

@Repository
public interface PersonRepository extends ReactiveSortingRepository<DbPerson, Long> {

    Mono<DbPerson> findByIdent(String ident);

    Mono<DbPerson> findById(Long id);

    Flux<DbPerson> findByIdentIn(Collection<String> identer);

    Flux<DbPerson> findByIdIn(List<Long> identer);

    @Modifying
    Flux<Void> deleteByIdentIn(List<String> ident);

    Mono<Boolean> existsByIdent(String ident);

    @Query("from DbPerson p "
            + "where (:partialIdent is null or :partialIdent is not null and p.ident like %:partialIdent%)"
            + "and (:partialNavn1 is null or :partialNavn1 is not null and (upper(p.etternavn) like %:partialNavn1% or upper(p.fornavn) like %:partialNavn1%))"
            + "and (:partialNavn2 is null or :partialNavn2 is not null and (upper(p.etternavn) like %:partialNavn2% or upper(p.fornavn) like %:partialNavn2%))")
    Flux<DbPerson> findByWildcardIdent(@Param("partialIdent") String partialIdent,
                                       @Param("partialNavn1") String partialNavn1,
                                       @Param("partialNavn2") String partialNavn2,
                                       Pageable pageable);
}
