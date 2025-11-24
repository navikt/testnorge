package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Testgruppe;
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
}
