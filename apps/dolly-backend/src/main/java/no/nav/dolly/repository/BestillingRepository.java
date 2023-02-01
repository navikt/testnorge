package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingFragment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BestillingRepository extends CrudRepository<Bestilling, Long> {

    @Query(value = "select b.id, g.navn " +
            "from Bestilling b " +
            "join Gruppe g " +
            "on b.gruppe_id = g.id " +
            "where length(:id) > 0 " +
            "and cast(b.id as VARCHAR) " +
            "ilike :id fetch first 10 rows only",
            nativeQuery = true)
    List<RsBestillingFragment> findByIdContaining(String id);

    @Query(value = "select b.id, g.navn " +
            "from Bestilling b " +
            "join Gruppe g " +
            "on b.gruppe_id = g.id " +
            "where length(:gruppenavn) > 0 " +
            "and g.navn " +
            "ilike :gruppenavn fetch first 10 rows only",
            nativeQuery = true)
    List<RsBestillingFragment> findByGruppenavnContaining(String gruppenavn);

    Bestilling save(Bestilling bestilling);

    @Query(value = "select position-1 " +
            "from ( " +
            "select b.id, row_number() over (order by b.id desc) as position " +
            "from bestilling b " +
            "where b.gruppe_id = :gruppeId" +
            ") result " +
            "where id = :bestillingId",
            nativeQuery = true)
    Optional<Integer> getPaginertBestillingIndex(@Param("bestillingId") Long bestillingId, @Param("gruppeId") Long gruppe);

    @Query(value = "from Bestilling b join BestillingProgress bp on b.id = bp.bestilling.id where bp.ident = :ident order by b.id asc")
    List<Bestilling> findBestillingerByIdent(@Param("ident") String ident);

    @Query(value = "from Bestilling b " +
            "join BestillingProgress bp on b.id = bp.bestilling.id " +
            "and bp.ident in (:identer) order by b.id asc")
    List<Bestilling> findBestillingerByIdentIn(@Param("identer") Collection<String> identer);

    @Query(value = "from Bestilling b where b.malBestillingNavn is not null and b.malBestillingNavn = :malNavn and b.bruker = :bruker order by b.malBestillingNavn")
    Optional<List<Bestilling>> findMalBestillingByMalnavnAndUser(@Param("bruker") Bruker bruker, @Param("malNavn") String malNavn);

    @Query(value = "from Bestilling b where b.malBestillingNavn is not null and b.bruker = :bruker order by b.malBestillingNavn")
    Optional<List<Bestilling>> findMalBestillingByUser(@Param("bruker") Bruker bruker);

    @Query(value = "select b from Bestilling b " +
            "join BestillingProgress bp on bp.bestilling.id = b.id " +
            "where b.gruppe = :gruppe " +
            "and exists (select b1 from BestillingProgress b1 where b1.bestilling.id = b.id and (b1.master = 'PDL' or b1.master = 'PDLF')) " +
            "order by b.id desc")
    Page<Bestilling> getBestillingerFromGruppe(@Param(value = "gruppe") Testgruppe testgruppe, Pageable pageable);

    @Query(value = "from Bestilling b where b.malBestillingNavn is not null order by b.malBestillingNavn")
    Optional<List<Bestilling>> findMalBestilling();

    @Modifying
    @Query(value = "delete from Bestilling b where b.gruppe.id = :gruppeId and b.malBestillingNavn is null")
    int deleteByGruppeIdExcludeMaler(@Param("gruppeId") Long gruppeId);

    @Modifying
    @Query(value = "update Bestilling b " +
            "set b.gruppe = null, b.opprettetFraGruppeId = null " +
            "where b.gruppe.id = :gruppeId")
    int updateBestillingNullifyGruppe(@Param("gruppeId") Long gruppeId);

    @Modifying
    @Query(value = "delete from Bestilling b " +
            "where b.id = :bestillingId " +
            "and b.malBestillingNavn is null " +
            "and not exists (select bp from BestillingProgress bp where bp.bestilling.id = :bestillingId)")
    int deleteBestillingWithNoChildren(@Param("bestillingId") Long bestillingId);

    @Modifying
    @Query(value = "update Bestilling b set b.ident = :newIdent where b.ident = :oldIdent")
    void swapIdent(@Param(value = "oldIdent") String oldIdent, @Param(value = "newIdent") String newIdent);
}
