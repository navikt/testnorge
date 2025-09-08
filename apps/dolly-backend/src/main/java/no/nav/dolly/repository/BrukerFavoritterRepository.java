package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.BrukerFavoritter;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BrukerFavoritterRepository extends ReactiveCrudRepository<BrukerFavoritter, Long> {

    @Query(""" 
            select * from bruker br
            join bruker_favoritter bf on bf.bruker_id = br.id
            and bf.gruppe_id = :gruppeId
            """)
    Flux<Bruker> getAllByGruppeId(@Param("gruppeId") Long gruppeId);

    @Query(""" 
            select * from bruker_favoritter bf
            where bf.bruker_id = :brukerId
            """)
    Flux<BrukerFavoritter> findByBrukerId(@Param("brukerId") Long brukerId);

    @Modifying
    @Query("""
            delete from bruker_favoritter
                   where bruker_id = :brukerId
                   and gruppe_id = :gruppeId
            """)
    Mono<Void> deleteByBrukerIdAndGruppeId(@Param("brukerId") Long brukerId, @Param("gruppeId") Long gruppeId);
}