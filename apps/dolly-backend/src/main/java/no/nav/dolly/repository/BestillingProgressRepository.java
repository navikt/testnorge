package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.BestillingProgress;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface BestillingProgressRepository extends ReactiveCrudRepository<BestillingProgress, Long> {

    Flux<BestillingProgress> findByBestillingId(Long bestillingId);

    @Modifying
    @Query("""
            delete from bestilling_progress bp 
                   where bp.bestilling_id in (select b.id 
                                              from Bestilling b where b.gruppe_id = :gruppeId)
            """)
    Mono<Void> deleteByGruppeId(@Param("gruppeId") Long gruppeId);

    @Modifying
    Mono<Void> deleteByIdent(String ident);

    @Modifying
    Mono<Mono> deleteByBestillingId(Long bestillingId);

    Flux<BestillingProgress> findByIdent(String ident);

    @Modifying
    @Query("""
            update bestilling_progress
                        set ident = :newIdent
                        where ident = :oldIdent
            """)
    Mono<BestillingProgress> swapIdent(@Param(value = "oldIdent") String oldIdent, @Param(value = "newIdent") String newIdent);

    @Query(value = "select * from bestilling_progress where id = :id for update")
    Mono<BestillingProgress> findByIdAndLock(@Param("id") Long id);
}
