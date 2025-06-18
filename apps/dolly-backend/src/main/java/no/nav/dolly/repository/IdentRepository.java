package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Testident;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

public interface IdentRepository extends ReactiveCrudRepository<Testident, Long> {

    Mono<Testident> findByIdent(String ident);

    Flux<Testident> findByIdentIn(Collection<String> identer);

    Mono<Boolean> existsByIdent(String ident);

    @Modifying
    @Query("""
            delete from test_ident ti where ti.ident = :testident
            """)
    Mono<Void> deleteTestidentByIdent(@Param("testident") String testident);

    @Modifying
    Mono<Void> deleteByGruppeId(Long gruppeId);

    @Modifying
    @Query(""" 
            update test_ident ti
            set ident = :newIdent
            where ti.ident = :oldIdent
            """)
    Mono<Testident> swapIdent(@Param(value = "oldIdent") String oldIdent, @Param(value = "newIdent") String newIdent);

    @Query("""
            select bp.ident as ident, b.id as bestillingId,
            b.best_kriterier as bestkriterier, b.miljoer as miljoer
            from bestilling b
            join bestilling_progress bp on bp.bestilling_id = b.id
            and b.gruppe_id = :gruppeId
            """)
    Flux<GruppeBestillingIdent> getBestillingerFromGruppe(@Param(value = "gruppeId") Long gruppeId);

    @Query("""
            select bp.ident as ident, b.id as bestillingId,
            b.best_kriterier as bestkriterier, b.miljoer as miljoer from bestilling b
            join bestilling_progress bp on bp.bestilling_id = b.id
            and bp.ident = :ident
            """)
    Flux<GruppeBestillingIdent> getBestillingerByIdent(@Param(value = "ident") String ident);

    @Query("""
            select ti from test_ident ti
            where ti.tilhoerer_gruppe = :gruppeId
            """)
    Flux<Testident> findAllByTestgruppeId(@Param(value = "gruppeId") Long gruppeId, Pageable pageable);

    Mono<Long> countByTestgruppeId(Long id);

    @Query("""
            select position-1
            from (select ti.ident, row_number() over (order by ti.id desc) as position
                  from test_ident ti
                  where ti.tilhoerer_gruppe = :gruppeId
                 ) result
            where ident = :ident
            """)
    Mono<Integer> getPaginertTestidentIndex(@Param("ident") String ident, @Param("gruppeId") Long gruppe);


    Mono<Long> countByGruppeIdAndIBruk(Long id, Boolean iBruk);

    interface GruppeBestillingIdent {

        String getIdent();

        Long getBestillingId();

        String getBestkriterier();

        String getMiljoer();
    }

    @Query("""
            select count(*) from test_ident ti where ti.tilhoerer_gruppe = :gruppeId
            """)
    Mono<Integer> countByTestgruppe(@Param("gruppeId") Long gruppeId);

    @Query("""
            select ti from test_ident ti where ti.tilhoerer_gruppe = :gruppeId
            """)
    Flux<Testident> findByTestgruppe(@Param("gruppeId") Long gruppeId);
}