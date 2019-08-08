package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Testident;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IdentRepository extends Repository<Testident, String> {

    Testident save(Testident testident);

    List<Testident> saveAll(Iterable<Testident> testidents);

    @Modifying
    @Query(value = "delete from Testident ti "
            + "where ti.ident in "
            + "(select ident from BestillingProgress bp "
            + "where bp.bestillingId = :bestillingId)")
    int deleteTestidentsByBestillingId(@Param("bestillingId") Long bestillingId);

    @Modifying
    int deleteTestidentByIdent(String testident);

    @Modifying
    int deleteTestidentByTestgruppeId(Long gruppeId);

    @Modifying
    @Query(value = "delete from T_TEST_IDENT ti where ti.tilhoerer_gruppe in (select tg.id from T_GRUPPE tg where tg.tilhoerer_team=:teamId)", nativeQuery = true)
    int deleteTestidentByTestgruppeTeamtilhoerighetId(@Param("teamId") Long teamId);
}
