package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bruker;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BrukerRepository extends ReactiveCrudRepository<Bruker, Long> {

    @Query("""
            select *
            from Bruker b
            where b.brukertype='AZURE'
            order by b.brukernavn
            """)
    Flux<Bruker> findByOrderById();

    Flux<Bruker> findByBrukerIdInOrderByBrukernavn(List<String> brukerId);

    @Query("""
            select * from bruker b
            where b.bruker_id = :brukerId
            """)
    Mono<Bruker> findByBrukerId(String brukerId);

    @Modifying
    @Query("""
            delete from BRUKER_FAVORITTER where gruppe_id = :groupId
            """)
    Mono<Integer> deleteBrukerFavoritterByGroupId(@Param("groupId") Long groupId);
}