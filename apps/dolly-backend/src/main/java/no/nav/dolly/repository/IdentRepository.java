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
    @Query(value = "delete from Testident ti where ti.ident = :testident")
    int deleteTestidentByIdent(@Param("testident") String testident);

    @Modifying
    @Query(value = "delete from Testident ti where ti.id = :id")
    void deleteById(@Param("id") Long id);

    @Modifying
    @Query(value = "delete from Testident ti where ti.testgruppe.id = :gruppeId")
    int deleteAllByTestgruppeId(@Param("gruppeId") Long gruppeId);

    @Modifying
    @Query(value = "update Testident ti set ti.ident = :newIdent where ti.ident = :oldIdent")
    int swapIdent(@Param(value = "oldIdent") String oldIdent, @Param(value = "newIdent") String newIdent);

    @Query(value = "select bp.ident as ident, b.id as bestillingId, " +
            "b.bestKriterier as bestkriterier, b.miljoer as miljoer from Bestilling b " +
            "join BestillingProgress bp on bp.bestilling.id = b.id " +
            "and b.gruppe = :gruppe ")
    List<GruppeBestillingIdent> getBestillingerFromGruppe(@Param(value = "gruppe") Testgruppe testgruppe);

    @Query(value = "select bp.ident as ident, b.id as bestillingId, " +
            "b.bestKriterier as bestkriterier, b.miljoer as miljoer from Bestilling b " +
            "join BestillingProgress bp on bp.bestilling.id = b.id " +
            "and bp.ident = :ident")
    List<GruppeBestillingIdent> getBestillingerByIdent(@Param(value = "ident") String ident);

    @Query("select ti from Testident ti " +
            "where ti.testgruppe.id = :gruppeId ")
    Page<Testident> findAllByTestgruppeId(@Param(value = "gruppeId") Long gruppeId, Pageable pageable);

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

        Long getBestillingId();

        String getBestkriterier();

        String getMiljoer();
    }

    @Query(value = "select count(*) from Testident ti where ti.testgruppe.id = :gruppeId")
    int countByTestgruppe(@Param("gruppeId") Long gruppeId);

    @Query(value = "select ti from Testident ti where ti.testgruppe.id = :gruppeId")
    List<Testident> findByTestgruppe(@Param("gruppeId") Long gruppeId);
}