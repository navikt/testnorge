package no.nav.dolly.regression.testrepositories;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Team;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamTestRepository extends CrudRepository<Team, Long> {

    @Query("FROM Team t"
            + " LEFT JOIN FETCH t.medlemmer"
            + " WHERE t.id=:id"
    )
    Team findByIdFetchMedlemmerEagerly(@Param("id") Long id);

    void deleteAll();

    List<Team> findAllByOrderByNavn();

    List<Team> findTeamsByEierOrderByNavn(Bruker standardBruker);
}
