package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.projection.GruppeFragment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface TestgruppeRepository extends ReactiveSortingRepository<Testgruppe, Long> {

    Flux<Testgruppe> findByOpprettetAvIdOrderByIdDesc(Long brukerId, Pageable pageable);

    Mono<Long> countByOpprettetAvId(Long brukerId);

    Flux<Testgruppe> findByOrderByIdDesc(Pageable pageable);

    Mono<Void> deleteAll();

    @Modifying
    Mono<Void> deleteById(Long id);

    @Query("""
            select tg.* from gruppe tg
            join bruker b on b.id = tg.opprettet_av
            where b.bruker_id in (:brukere)
            """)
    Flux<Testgruppe> findByOpprettetAv_BrukerIdIn(@Param("brukere") List<String> brukere, Pageable pageable);

    @Query("""
            select count(tg.id) from gruppe tg
            join bruker b on b.id = tg.opprettet_av
            where b.bruker_id in (:brukere)
            """)
    Mono<Long> countByOpprettetAv_BrukerIdIn(@Param("brukere") List<String> brukere);

    Mono<Long> countBy();

    @Query("""
            select tg.* from gruppe tg
            join bestilling b on b.gruppe_id = tg.id
            where b.id = :bestillingId
            """)
    Mono<Testgruppe> findByBestillingId(@Param("bestillingId") Long bestillingId);

    Mono<Testgruppe> findById(Long id);

    Flux<Testgruppe> findByIdIn(List<Long> identer);

    @Modifying
    Mono<Testgruppe> save(Testgruppe testgruppe);

    @Query("""
        select g.id as id, g.navn as navn
        from Gruppe g
        join Bruker b on b.id = g.opprettet_av
        where (
            coalesce(cardinality(:brukerIds), 0) = 0
            or cast(b.bruker_id as text) = any(:brukerIds)
        )
        and (
            :id is null
            or length(:id) = 0
            or cast(g.id as varchar) ilike concat('%', :id, '%')
        )
        and (
            :gruppenavn is null
            or length(:gruppenavn) = 0
            or g.navn ilike concat('%', :gruppenavn, '%')
        )
        order by g.id
        fetch first 10 rows only
        """)
    Flux<GruppeFragment> findByIdAndNavnAndBrukere(@Param("id") String id,
                                                   @Param("gruppenavn") String gruppenavn,
                                                   @Param("brukerIds") String[] brukerIds);
}
