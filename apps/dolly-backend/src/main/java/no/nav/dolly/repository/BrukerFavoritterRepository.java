package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.BrukerFavoritter;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.Set;

public interface BrukerFavoritterRepository extends ReactiveCrudRepository<BrukerFavoritter, Long> {

    @Query(value = "select * from bruker br " +
            "join bruker_favoritter bf on bf.bruker_id = br.id " +
            "and bf.gruppe_id = :gruppeId", nativeQuery = true)
    Flux<Bruker> getAllByGruppeId(@Param("gruppeId") Long gruppeId);
}