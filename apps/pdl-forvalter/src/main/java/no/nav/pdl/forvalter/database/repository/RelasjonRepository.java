package no.nav.pdl.forvalter.database.repository;

import no.nav.pdl.forvalter.database.model.DbRelasjon;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelasjonType;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;

@Repository
public interface RelasjonRepository extends ReactiveCrudRepository<DbRelasjon, Long> {

    Mono<DbRelasjon> findByPersonIdAndRelatertPersonIdAndRelasjonType(Long id1, Long id2, RelasjonType relasjonType);

    @Modifying
    @Query("delete from DbRelasjon r " +
           "where r.person.id in (select p.id from DbPerson p where p.ident in (:identer))")
    Mono<Void> deleteByPersonIdentIn(Collection<String> identer);

    Flux<DbRelasjon> findByPersonId(Long id);
    Flux<DbRelasjon> findByPersonIdIn(List<Long> ids);

    @Query("""
            select exists (select 1 from DbRelasjon r
            where r.person.id = :id or r.relatertPerson.id = :id)
    """)
    Mono<Boolean> existsByPersonIdOrRelatertPersonId(Long id);

    @Query("""
            select r from DbRelasjon r
            where (r.personId = :personId or r.relatertPersonId = :personId)
            and r.relasjonType = :relasjonType
            """)
    Flux<DbRelasjon> findByPersonIdOrRelatertPersonIdAndRelasjonType(Long personId, RelasjonType relasjonType);

    @Modifying
    Mono<Void> deleteByPersonIdAndRelatertPersonId(Long personId, Long relatertPersonId);

    @Modifying
    @Query("""
            delete from DbRelasjon r
            where r.person.id = :person_id or r.relatertPerson.id = :personId
            """)
    Mono<Void> deleteByPersonIdOrRelatertPersonId(Long personId);

    @Modifying
    @Query("""
            delete from DbRelasjon r
            where (r.person.id = :personId or r.relatertPerson.id = :relatertPersonId)
            and r.relasjonType = :relasjonType
            """)
    Mono<Void> deleteByPersonIdAndRelatertPersonIdAndRelasjonType(Long personId, Long relatertPersonId, RelasjonType relasjonType);
}
