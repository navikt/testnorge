package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.projection.RsBestillingFragment;
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
public interface BestillingRepository extends ReactiveSortingRepository<Bestilling, Long> {

    Mono<Bestilling> findById(Long id);

    Flux<Bestilling> findBy();

    Mono<Void> deleteById(Long id);

    Flux<Bestilling> findByGruppeId(Long gruppeId);

    Mono<Integer> countByGruppeId(Long gruppeId);

    @Query("""
            select b.id as id, g.navn as navn
            from Bestilling b
            join Gruppe g on b.gruppe_id = g.id
            where length(:id) > 0
            and cast(b.id as VARCHAR) ilike :id
            fetch first 10 rows only
            """)
    Flux<RsBestillingFragment> findByIdContaining(@Param("id") String id);

    @Query("""
            select b.id as id, g.navn as navn
            from Bestilling b
            join Gruppe g on b.gruppe_id = g.id
            where length(:gruppenavn) > 0
            and g.navn ilike :gruppenavn
            fetch first 10 rows only
            """)
    Flux<RsBestillingFragment> findByGruppenavnContaining(@Param("gruppenavn") String gruppenavn);

    @Query("""
            select b.id as id, g.navn as navn
            from Bestilling b
            join gruppe g on b.gruppe_id = g.id
            where cast(b.id as varchar) like :id
            and g.navn like :gruppenavn
            fetch first 10 rows only
            """)
    Flux<RsBestillingFragment> findByIdContainingAndGruppeNavnContaining(
            @Param("id") String id,
            @Param("gruppenavn") String gruppenavn
    );

    Mono<Bestilling> save(Bestilling bestilling);

    @Query("""
            select position-1
            from (
            select b.id, row_number() over (order by b.id desc) as position
            from bestilling b
            where b.gruppe_id = :gruppeId
            ) result
            where id = :bestillingId
            """)
    Mono<Integer> getPaginertBestillingIndex(@Param("bestillingId") Long bestillingId, @Param("gruppeId") Long gruppe);

    @Query("""
            select * from Bestilling b
            join Bestilling_Progress bp on b.id = bp.bestilling_id
            where bp.ident = :ident
            order by b.id
            """)
    Flux<Bestilling> findBestillingerByIdent(@Param("ident") String ident);

    @Query("""
            select b.* from Bestilling b
            join Bestilling_Progress bp on b.id = bp.bestilling_id
            and bp.ident in (:identer) order by b.id
            """)
    Flux<Bestilling> findBestillingerByIdentIn(@Param("identer") Collection<String> identer);

    Flux<Bestilling> findByGruppeIdOrderByIdDesc(Long gruppeId, Pageable pageable);

    @Modifying
    Mono<Void> deleteByGruppeId(Long gruppeId);

    @Modifying
    @Query("""
            delete from Bestilling b
            where b.id = :bestillingId
            and not exists (select *
                            from Bestilling_Progress bp
                            where bp.bestilling_id = :bestillingId)
            and not exists (select *
                            from transaksjon_mapping tm
                            where tm.bestilling_id = :bestillingId)
            """)
    Mono<Void> deleteBestillingWithNoChildren(@Param("bestillingId") Long bestillingId);

    @Modifying
    @Query("""
            update Bestilling b
            set ident = :newIdent where ident = :oldIdent
            """)
    Mono<Bestilling> swapIdent(@Param(value = "oldIdent") String oldIdent, @Param(value = "newIdent") String newIdent);

    @Query("""
            select * from bestilling b
            where id = :id for update
            """)
    Mono<Bestilling> findByIdAndLock(@Param("id") Long id);

    @Modifying
    @Query("""
            update Bestilling b
            set ferdig = true,
            stoppet = true
            where b.ferdig = false
            """)
    Mono<Integer> stopAllUnfinished();

    Flux<Bestilling> findByIdIn(List<Long> id);
}