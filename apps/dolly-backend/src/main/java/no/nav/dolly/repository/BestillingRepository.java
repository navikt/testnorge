package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingFragment;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BestillingRepository extends Repository<Bestilling, Long> {

    @Query("from Bestilling b where b.id = :id")
    Optional<Bestilling> findById(@Param("id") Long id);

    @Query(value = "select b.id, g.navn from Bestilling b inner join Gruppe g on b.gruppe_id = g.id where cast(b.id as VARCHAR) like %:id% fetch first 20 rows only", nativeQuery = true)
    Optional<List<RsBestillingFragment>> findByIdContaining(String id);

    Bestilling save(Bestilling bestilling);

    @Query(value = "from Bestilling b where b.malBestillingNavn is not null and b.malBestillingNavn = :malNavn and b.bruker = :bruker order by b.malBestillingNavn")
    Optional<List<Bestilling>> findMalBestillingByMalnavnAndUser(@Param("bruker") Bruker bruker, @Param("malNavn") String malNavn);

    @Query(value = "from Bestilling b where b.malBestillingNavn is not null and b.bruker = :bruker order by b.malBestillingNavn")
    Optional<List<Bestilling>> findMalBestillingByUser(@Param("bruker") Bruker bruker);

    @Query(value = "from Bestilling b where b.malBestillingNavn is not null order by b.malBestillingNavn")
    Optional<List<Bestilling>> findMalBestilling();

    int deleteByGruppeId(Long gruppeId);

    @Modifying
    @Query(value = "delete from Bestilling b where b.id = :bestillingId and not exists " +
            "(select bp from BestillingProgress bp where bp.bestilling.id = :bestillingId)")
    int deleteBestillingWithNoChildren(@Param("bestillingId") Long bestillingId);

    @Modifying
    @Query(value = "update Bestilling b set b.ident = :newIdent where b.ident = :oldIdent")
    void swapIdent(@Param(value = "oldIdent") String oldIdent, @Param(value = "newIdent") String newIdent);
}
