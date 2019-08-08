package no.nav.dolly.testrepositories;

import no.nav.dolly.domain.jpa.Bruker;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BrukerTestRepository extends CrudRepository<Bruker, Long> {

    @Query("FROM Bruker b"
            + " LEFT JOIN FETCH b.teams"
            + " WHERE b.navIdent=:navIdent"
    )
    Bruker findByNavIdentTeamsEagerly(@Param("navIdent") String navIdent);

    void deleteAll();

    List<Bruker> findAllByOrderByNavIdent();

    Bruker findBrukerByNavIdent(String standardNavIdent);

    void flush();
}