package no.nav.dolly.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import no.nav.dolly.domain.jpa.Bruker;

public interface BrukerRepository extends Repository<Bruker, Long> {

    Bruker save(Bruker bruker);

    List<Bruker> findAllByOrderById();

    Optional<Bruker> findBrukerByBrukerId(String brukerId);

    Optional<Bruker> findBrukerByNavIdent(String navIdent);

    @Modifying
    @Query(value = "delete from T_BRUKER_FAVORITTER where gruppe_id = :groupId", nativeQuery = true)
    int deleteBrukerFavoritterByGroupId(@Param("groupId") Long groupId);
}