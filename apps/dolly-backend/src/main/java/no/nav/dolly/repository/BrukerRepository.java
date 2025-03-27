package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bruker;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BrukerRepository extends Repository<Bruker, Long> {

    Bruker save(Bruker bruker);

    List<Bruker> findAllByOrderById();

    List<Bruker> findAllByBrukerIdIn(List<String> brukerId);

    Optional<Bruker> findBrukerByBrukerId(String brukerId);

    Optional<Bruker> findBrukerByNavIdent(String navIdent);

    @Modifying
    @Query(value = "delete from BRUKER_FAVORITTER where gruppe_id = :groupId", nativeQuery = true)
    int deleteBrukerFavoritterByGroupId(@Param("groupId") Long groupId);

    @Query(value = "from Bruker b where b.eidAv = :bruker")
    List<Bruker> fetchEidAv(@Param("bruker") Bruker bruker);

}