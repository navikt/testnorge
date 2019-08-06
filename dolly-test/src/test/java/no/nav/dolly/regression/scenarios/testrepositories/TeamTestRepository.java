package no.nav.dolly.regression.scenarios.testrepositories;

import no.nav.dolly.domain.jpa.Team;
import no.nav.dolly.repository.TeamRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TeamTestRepository extends TeamRepository {

    @Query("FROM Team t"
            + " LEFT JOIN FETCH t.medlemmer"
            + " WHERE t.id=:id"
    )
    Team findByIdFetchMedlemmerEagerly(@Param("id") Long id);

    void deleteAll();
}
