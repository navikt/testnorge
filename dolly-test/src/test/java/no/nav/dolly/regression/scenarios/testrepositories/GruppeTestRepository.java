package no.nav.dolly.regression.scenarios.testrepositories;

import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.repository.GruppeRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GruppeTestRepository extends GruppeRepository {

    @Query("FROM Testgruppe t"
            + " LEFT JOIN FETCH t.testidenter"
            + " WHERE t.id=:id"
    )
    Testgruppe findByIdFetchIdenterEagerly(@Param("id") Long id);
}
