package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.OrganisasjonBestilling;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrganisasjonBestillingRepository extends CrudRepository<OrganisasjonBestilling, Long> {

    @Modifying
    @Query(value = "delete from OrganisasjonBestilling b where b = :bestilling and not exists (select bp from OrganisasjonBestillingProgress bp where bp.bestilling = :bestilling)")
    int deleteBestillingWithNoChildren(@Param("bestilling") OrganisasjonBestilling bestilling);

    List<OrganisasjonBestilling> findByBruker(Bruker bruker);

    @Modifying
    @Query("""
        update OrganisasjonBestilling ob
        set ob.ferdig = true
        where ob.ferdig = false
""")

    int stopAllUnfinished();

}