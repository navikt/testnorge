package no.nav.identpool.repository.oracle;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import no.nav.identpool.domain.postgres.Ident;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Kjoenn;
import no.nav.identpool.domain.postgres.Rekvireringsstatus;
import no.nav.identpool.domain.oracle.OraIdent;

public interface OraIdentRepository extends PagingAndSortingRepository<OraIdent, Long> {

    boolean existsByPersonidentifikator(String identifikator);

    long countByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatus(LocalDate from, LocalDate to, Identtype type, Rekvireringsstatus rekvireringsstatus);

    Ident findTopByPersonidentifikator(String personidentifkator);

    @Modifying
    Ident save(Ident ident);

    List<Ident> findByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatus(LocalDate from, LocalDate to, Identtype type, Rekvireringsstatus rekvireringsstatus);

    long countByRekvireringsstatusAndIdenttype(Rekvireringsstatus rekvireringsstatus, Identtype identtype);

    @Modifying
    void deleteAll();

    @Query(value = "from Ident i where i.rekvireringsstatus = :rekvireringsstatus and i.foedselsdato > :foedtEtter")
    Page<Ident> findAll(@Param("rekvireringsstatus") Rekvireringsstatus rekvireringsstatus,
            @Param("foedtEtter") LocalDate foedtEtter, Pageable pageable);

    @Query(value = "from Ident i where i.rekvireringsstatus = :rekvireringsstatus and i.kjoenn = :kjoenn and "
            + "i.identtype = :identtype and i.foedselsdato between :foedtFoer and :foedtEtter")
    Page<Ident> findAll(@Param("rekvireringsstatus") Rekvireringsstatus rekvireringsstatus,
            @Param("identtype") Identtype identtype, @Param("kjoenn") Kjoenn kjoenn,
            @Param("foedtFoer") LocalDate foedtfoer, @Param("foedtEtter") LocalDate foedtEtter, Pageable pageable);
}
