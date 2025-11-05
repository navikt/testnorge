package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bruker;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface BrukerRepository extends ReactiveCrudRepository<Bruker, Long> {

    @Query("""
            select *
            from Bruker b
            where b.brukertype='AZURE' OR b.brukertype='TEAM'
            order by b.brukernavn
            """)
    Flux<Bruker> findByOrderById();

    Flux<Bruker> findByBrukerIdInOrderByBrukernavn(List<String> brukerId);

    @Query("""
            select * from bruker b
            where b.bruker_id = :brukerId
            """)
    Mono<Bruker> findByBrukerId(String brukerId);

    Mono<Bruker> findByBrukernavn(String navn);

    @Modifying
    @Query("""
            delete from bruker_favoritter bf
            where bf.gruppe_id = :groupId
            """)
    Mono<Void> deleteBrukerFavoritterByGroupId(@Param("groupId") Long groupId);

    Flux<Bruker> findByIdIn(List<Long> brukerIds);

    Flux<Bruker> findByBrukerIdIn(List<String> brukere);
}