package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingFragment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

public interface BestillingRepository extends ReactiveSortingRepository<Bestilling, Long> {

    Mono<Bestilling> findById(Long id);

    Mono<Void> deleteById(Long id);

    Flux<Bestilling> findBestillingByGruppeId(Long gruppeId);
    Mono<Long> countAllByGruppeId(Long gruppeId);

    @Query("""
            select b.id, g.navn
            from Bestilling b
            join Gruppe g on b.gruppe_id = g.id
            where length(:id) > 0
            and cast(b.id as VARCHAR) ilike :id fetch first 10 rows only
            """)
    Flux<RsBestillingFragment> findByIdContaining(String id);

    @Query("""
            select b.id, g.navn
            from Bestilling b
            join Gruppe g on b.gruppe_id = g.id
            where length(:gruppenavn) > 0
            and g.navn ilike :gruppenavn
            fetch first 10 rows only
            """)
    Flux<RsBestillingFragment> findByGruppenavnContaining(String gruppenavn);

    @Query("""
            select b.id as id, g.navn as navn from Bestilling b
            join gruppe g on b.gruppe_id = g.id
            where cast(b.id as varchar) like '%:id%'
            and g.navn like '%:gruppenavn%'
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
            select b from Bestilling b
            join Bestilling_Progress bp on b.id = bp.bestilling_id
            where bp.ident = :ident
            order by b.id asc
            """)
    Flux<Bestilling> findBestillingerByIdent(@Param("ident") String ident);

    @Query("""
            select b from Bestilling b
            join Bestilling_Progress bp on b.id = bp.bestilling_id
            and bp.ident in (:identer) order by b.id asc
            """)
    Flux<Bestilling> findBestillingerByIdentIn(@Param("identer") Collection<String> identer);

    @Query("""
            select distinct(b) from Bestilling b
            where b.gruppe_id = :gruppeId
            order by b.id desc
            """)
    Flux<Bestilling> getBestillingerFromGruppeId(@Param(value = "gruppeId") Long gruppeId, Pageable pageable);

    @Modifying
    Mono<Void> deleteByGruppeId(Long gruppeId);

    @Modifying
    @Query("""
            update Bestilling b
            set gruppe_id = null, opprettet_fra_id = null, bruker_id = null
            where b.gruppe_id = :gruppeId
            """)
    Flux<Bestilling> updateBestillingNullifyGruppe(@Param("gruppeId") Long gruppeId);

    @Modifying
    @Query("""
            delete from Bestilling b
            where b.id = :bestillingId
            and not exists (select bp
                            from Bestilling_Progress bp
                            where bp.bestilling_id = :bestillingId)
            """)
    Mono<Void> deleteBestillingWithNoChildren(@Param("bestillingId") Long bestillingId);

    @Modifying
    @Query("""
            update Bestilling b
            set ident = :newIdent where ident = :oldIdent
            """)
    Mono<Bestilling> swapIdent(@Param(value = "oldIdent") String oldIdent, @Param(value = "newIdent") String newIdent);

    @Query("""
            select b from bestilling b
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
    Mono<Bestilling> stopAllUnfinished();
}