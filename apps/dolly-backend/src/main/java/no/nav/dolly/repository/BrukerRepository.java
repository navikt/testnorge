package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BrukerRepository extends Repository<Bruker, Long> {

    Bruker save(Bruker bruker);

    @Query(value = "from Bruker b where b.brukertype='AZURE' order by b.brukernavn")
    List<Bruker> findAllByOrderById();

    @Query(value = "from Bruker b where b.brukertype='TEAM' and b.gjeldendeTeam.id = :teamId")
    Optional<Bruker> findTeamBrukerByGjeldendeTeam(Long teamId);

    List<Bruker> findAllByBrukerIdInOrderByBrukernavn(List<String> brukerId);

    Optional<Bruker> findBrukerByBrukerId(String brukerId);

    @Modifying
    @Query(value = "delete from BRUKER_FAVORITTER where gruppe_id = :groupId", nativeQuery = true)
    int deleteBrukerFavoritterByGroupId(@Param("groupId") Long groupId);

    List<Bruker> findBrukerByGjeldendeTeam(Team gjeldendeTeam);

    @Modifying
    @Query(value = "delete from Bruker b where b.id = :brukerId AND b.brukertype='TEAM'")
    void deleteBrukerTeamById(Long brukerId);

    Optional<Bruker> findBrukerById(Long brukerId);
}