package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface IdentRepository extends CrudRepository<Testident, String> {

    Testident findByIdent(String ident);

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
            "join BestillingProgress bp on bp.bestillingId = b.id " +
            "and b.gruppe = :gruppe " +
            "and b.opprettetFraId is null " +
            "and b.bestKriterier is not null and b.bestKriterier <> '{}' " +
            "and bp.ident is not null and length(bp.ident) = 11")
    List<GruppeBestillingIdent> getBestillingerFromGruppe(@Param(value = "gruppe") Testgruppe testgruppe);

    @Query("select ti from Testident ti " +
            "join BestillingProgress bp on bp.ident = ti.ident " +
            "join Bestilling b on b.id = bp.bestillingId " +
            "and ti.testgruppe.id = b.gruppe.id " +
            "and ti.testgruppe.id = :gruppe_id " +
            "order by b.sistOppdatert desc")
    Page<Testident> getBestillingerFromGruppePaginert(@Param(value = "gruppe_id") Long gruppeId, Pageable pageable);

    interface GruppeBestillingIdent {

        String getIdent();

        Long getBestillingid();

        String getBestkriterier();
    }
}