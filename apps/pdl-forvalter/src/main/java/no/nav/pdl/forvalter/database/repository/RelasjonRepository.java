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
    @Query("delete from relasjon r " +
           "where r.person_id in (select p.id from person p where p.ident in (:identer))")
    Mono<Void> deleteByPersonIdentIn(Collection<String> identer);

    Flux<DbRelasjon> findByPersonId(Long id);

    Flux<DbRelasjon> findByPersonIdIn(List<Long> ids);

    @Query("""
                    select exists (select 1 from relasjon r
                    where r.person_id = :id or r.relatert_person_id = :id)
            """)
    Mono<Boolean> existsByPersonIdOrRelatertPersonId(Long id);

    Flux<DbRelasjon> findByPersonIdAndRelasjonTypeIn(Long personId, List<RelasjonType> relasjonTyper);

    @Modifying
    Mono<Void> deleteByPersonIdAndRelatertPersonId(Long personId, Long relatertPersonId);

    @Modifying
    @Query("""
            delete from relasjon r
            where r.person_id = :personId or r.relatert_person_id = :personId
            """)
    Mono<Void> deleteByPersonIdOrRelatertPersonId(Long personId);

    @Modifying
    @Query("""
            delete from relasjon r
            where (r.person_id = :personId or r.relatert_person_id = :relatertPersonId)
            and r.relasjon_type = :relasjonType
            """)
    Mono<Void> deleteByPersonIdAndRelatertPersonIdAndRelasjonType(Long personId,
                                                                  Long relatertPersonId,
                                                                  RelasjonType relasjonType);
}
