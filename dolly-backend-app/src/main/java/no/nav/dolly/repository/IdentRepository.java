package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Testident;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface IdentRepository extends CrudRepository<Testident, String> {

    Testident findByIdent(String ident);

    Testident save(Testident testident);

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
