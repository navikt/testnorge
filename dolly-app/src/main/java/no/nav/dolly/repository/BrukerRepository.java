package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bruker;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BrukerRepository extends CrudRepository<Bruker, Long> {

    @Modifying
    Bruker save(Bruker bruker);

    Bruker findBrukerByNavIdent(String navIdent);

    List<Bruker> findByNavIdentInOrderByNavIdent(List<String> navIdenter);

    List<Bruker> findAllByOrderByNavIdent();

    @Modifying
    @Query(value = "delete from T_BRUKER_FAVORITTER where gruppe_id in (select id from t_gruppe where TILHOERER_TEAM = :teamId)", nativeQuery = true)
    int deleteBrukerFavoritterByTeamId(@Param("teamId") Long teamId);

    @Modifying
    @Query(value = "delete from T_BRUKER_FAVORITTER where gruppe_id = :groupId", nativeQuery = true)
    int deleteBrukerFavoritterByGroupId(@Param("groupId") Long groupId);
}