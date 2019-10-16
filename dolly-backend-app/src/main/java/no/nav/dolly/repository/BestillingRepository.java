package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Testgruppe;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BestillingRepository extends Repository<Bestilling, Long> {

    Optional<Bestilling> findById(Long id);

    Bestilling save(Bestilling bestilling);

    List<Bestilling> findBestillingByGruppeOrderById(Testgruppe gruppe);

    @Query(value = "from Bestilling b where b.malBestillingNavn is not null order by b.malBestillingNavn")
    Optional<List<Bestilling>> findMalBestilling();

    int deleteByGruppeId(Long gruppeId);

    @Modifying
    @Query(value = "delete from Bestilling b where b.id = :bestillingId and not exists (select bp from BestillingProgress bp where bp.bestillingId = :bestillingId)")
    int deleteBestillingWithNoChildren(@Param("bestillingId") Long bestillingId);
}
