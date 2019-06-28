package no.nav.dolly.regression.scenarios.testrepositories;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.repository.BrukerRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BrukerTestRepository extends BrukerRepository {

    @Query("FROM Bruker b"
            + " LEFT JOIN FETCH b.teams"
            + " WHERE b.navIdent=:navIdent"
    )
    Bruker findByNavIdentTeamsEagerly(@Param("navIdent") String navIdent);
}
