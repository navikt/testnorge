package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Testgruppe;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TestgruppeRepository extends ReactiveCrudRepository<Testgruppe, Long> {

    Flux<Testgruppe> findAllByOpprettetAvId(Long brukerId, Pageable pageable);

    Flux<Testgruppe> findAllOrderByIdDesc(Pageable pageable);
    Mono<Long> countAll();

    @Modifying
    @Query("""
            delete from Testgruppe tg where tg.id = :testgruppeId
            """)
    Mono<Integer> deleteAllById(@Param("testgruppeId") Long id);

    @Query("""
            select tg.* from gruppe tg " +
            join bruker b on tg.opprettet_av = b.id " +
            and b.bruker_Id in  (:brukere)
            """)
    Flux<Testgruppe> findAllByOpprettetAv_BrukerIdIn(@Param("brukere") List<String> brukere, Pageable id);

    @Query("""
            select tg.id from gruppe tg
            "join bruker b on tg.opprettet_av = b.id
            "and b.bruker_Id in  (:brukere)
            """)
    Flux<Long> findAllIdsByOpprettetAv_BrukerIdIn(@Param("brukere") List<String> brukere);
}
