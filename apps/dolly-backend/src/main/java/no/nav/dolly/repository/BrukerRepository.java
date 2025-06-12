package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bruker;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

public interface BrukerRepository extends ReactiveCrudRepository<Bruker, Long> {

    Mono<Bruker> save(Bruker bruker);

    @Query(value = "from Bruker b where b.brukertype='AZURE' order by b.brukernavn")
    List<Bruker> findAllByOrderById();

    List<Bruker> findAllByBrukerIdInOrderByBrukernavn(List<String> brukerId);

    Optional<Bruker> findBrukerByBrukerId(String brukerId);

    @Modifying
    @Query(value = "delete from BRUKER_FAVORITTER where gruppe_id = :groupId", nativeQuery = true)
    int deleteBrukerFavoritterByGroupId(@Param("groupId") Long groupId);
}