package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IdentRepository extends PagingAndSortingRepository<Testident, String> {

    Optional<Testident> findByIdent(String ident);

    List<Testident> findByIdentIn(Collection<String> identer);

    Testident save(Testident testident);

    @Modifying
    int deleteTestidentByIdent(String testident);

    @Modifying
    int deleteTestidentByTestgruppeId(Long gruppeId);

    @Modifying
    @Query(value = "update Testident ti set ti.ident = :newIdent where ti.ident = :oldIdent")
    int swapIdent(@Param(value = "oldIdent") String oldIdent, @Param(value = "newIdent") String newIdent);

    @Query(value = "select bp.ident as ident, b.id as bestillingid, " +
            "b.bestKriterier as bestkriterier from Bestilling b " +
            "join BestillingProgress bp on bp.bestilling.id = b.id " +
            "and b.gruppe = :gruppe " +
            "and b.opprettetFraId is null " +
            "and b.bestKriterier is not null and b.bestKriterier <> '{}' " +
            "and bp.ident is not null and length(bp.ident) = 11")
    List<GruppeBestillingIdent> getBestillingerFromGruppe(@Param(value = "gruppe") Testgruppe testgruppe);

    @Query("select ti from Testident ti " +
            "join BestillingProgress bp on bp.ident = ti.ident " +
            "and ti.testgruppe.id = :gruppeId " +
            "and bp.id = (select max(bps.id) from BestillingProgress bps where bps.ident = ti.ident) " +
            "order by bp.id desc, bp.bestilling.id desc")
    Page<Testident> getTestidentByTestgruppeIdOrderByBestillingProgressIdDesc(@Param(value = "gruppeId") Long gruppeId, Pageable pageable);

    @Query(value = "select idx-1 from ( " +
            "         select ti.*, count(*) over (order by bp.id desc, bp.bestilling_id desc ) as idx " +
            "         from test_ident ti, bestilling_progress bp " +
            "         where ti.ident = bp.ident " +
            "         and ti.tilhoerer_gruppe = :gruppeId " +
            "         and bp.id = (select max(bps.id) from bestilling_progress bps where bps.ident = ti.ident) " +
            "     ) x where ident = :ident",
            nativeQuery = true)
    Optional<Integer> getPaginertTestidentIndex(@Param("ident") String ident, @Param("gruppeId") Long gruppe);

    interface GruppeBestillingIdent {

        String getIdent();

        Long getBestillingid();

        String getBestkriterier();
    }
}