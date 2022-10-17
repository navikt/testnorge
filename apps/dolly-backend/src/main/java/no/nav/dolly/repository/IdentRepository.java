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

public interface IdentRepository extends PagingAndSortingRepository<Testident, Long> {

    Optional<Testident> findByIdent(String ident);

    List<Testident> findByIdentIn(Collection<String> identer);

    Testident save(Testident testident);

    boolean existsByIdent(String ident);

    @Modifying
    int deleteTestidentByIdent(String testident);

    @Override // nødvendig da eksplisitt query sikrer at sletting skjer før oppretting!
    @Modifying
    @Query(value = "delete from Testident ti where ti.id = :id")
    void deleteById(@Param("id") Long id);

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

    @Query(value = "select bp.ident as ident, b.id as bestillingid, " +
            "b.bestKriterier as bestkriterier from Bestilling b " +
            "join BestillingProgress bp on bp.bestilling.id = b.id " +
            "and b.gruppe = :gruppe " +
            "and b.opprettetFraId is null " +
            "and b.bestKriterier is not null and b.bestKriterier <> '{}' " +
            "and bp.ident is not null and length(bp.ident) = 11")
    Page<GruppeBestillingIdent> getBestillingerFromGruppe(@Param(value = "gruppe") Testgruppe testgruppe, Pageable pageable);

    @Query(value = "select bp.ident as ident, b.id as bestillingid, " +
            "b.bestKriterier as bestkriterier from Bestilling b " +
            "join BestillingProgress bp on bp.bestilling.id = b.id " +
            "and bp.ident = :ident " +
            "and b.opprettetFraId is null " +
            "and b.opprettetFraGruppeId is null " +
            "and b.gjenopprettetFraIdent is null " +
            "and b.bestKriterier is not null and b.bestKriterier <> '{}' ")
    List<GruppeBestillingIdent> getBestillingerByIdent(@Param(value = "ident") String ident);

    @Query("select ti from Testident ti " +
            "where ti.testgruppe.id = :gruppeId " +
            "order by ti.id desc")
    Page<Testident> getTestidentByTestgruppeIdOrderByBestillingProgressIdDesc(@Param(value = "gruppeId") Long gruppeId, Pageable pageable);

    @Query(value = "select position-1 " +
            "from ( " +
            "select ti.ident, row_number() over (order by ti.id desc) as position " +
            "from test_ident ti " +
            "where ti.tilhoerer_gruppe = :gruppeId" +
            ") result " +
            "where ident = :ident",
            nativeQuery = true)
    Optional<Integer> getPaginertTestidentIndex(@Param("ident") String ident, @Param("gruppeId") Long gruppe);

    interface GruppeBestillingIdent {

        String getIdent();

        Long getBestillingid();

        String getBestkriterier();
    }
}
