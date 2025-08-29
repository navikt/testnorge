package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingFragment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BestillingRepository extends JpaRepository<Bestilling, Long> {

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

    @Query("select b.id as id, g.navn as navn from Bestilling b " +
            "join b.gruppe g " +
            "where cast(b.id as string) like %:id% " +
            "and g.navn like %:gruppenavn%")
    List<RsBestillingFragment> findByIdContainingAndGruppeNavnContaining(
            @Param("id") String id,
            @Param("gruppenavn") String gruppenavn
    );

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

    @Query(value = "select distinct(b) from Bestilling b " +
            "where b.gruppe.id = :gruppeId " +
            "order by b.id desc")
    Page<Bestilling> getBestillingerFromGruppeId(@Param(value = "gruppeId") Long gruppeId, Pageable pageable);

    @Modifying
    @Query(value = "delete from Bestilling b where b.gruppe.id = :gruppeId")
    int deleteByGruppeId(@Param("gruppeId") Long gruppeId);

    @Modifying
    @Query(value = "update Bestilling b " +
            "set b.gruppe = null, b.opprettetFraGruppeId = null, b.bruker.id = null " +
            "where b.gruppe.id = :gruppeId")
    int updateBestillingNullifyGruppe(@Param("gruppeId") Long gruppeId);

    @Modifying
    @Query(value = "delete from Bestilling b " +
            "where b.id = :bestillingId " +
            "and not exists (select bp from BestillingProgress bp where bp.bestilling.id = :bestillingId)")
    int deleteBestillingWithNoChildren(@Param("bestillingId") Long bestillingId);

    @Modifying
    @Query(value = "update Bestilling b set b.ident = :newIdent where b.ident = :oldIdent")
    void swapIdent(@Param(value = "oldIdent") String oldIdent, @Param(value = "newIdent") String newIdent);

    @Query(value = "select * from bestilling where id = :id for update", nativeQuery = true)
    Optional<Bestilling> findByIdAndLock(@Param("id") Long id);

    @Modifying
    @Query("""
                    update Bestilling b
                    set b.ferdig = true,
                        b.stoppet = true
                    where b.ferdig = false
            """)
    int stopAllUnfinished();
}