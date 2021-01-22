package no.nav.identpool.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Kjoenn;
import no.nav.identpool.domain.Rekvireringsstatus;
import no.nav.identpool.domain.Ident;

public interface IdentRepository extends PagingAndSortingRepository<Ident, Long> {

    boolean existsByPersonidentifikator(String identifikator);

    long countByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatus(LocalDate from, LocalDate to, Identtype type, Rekvireringsstatus rekvireringsstatus);

    Ident findTopByPersonidentifikator(String personidentifkator);

    Set<Ident> findByPersonidentifikatorIn(Set<String> idents);

    @Modifying
    Ident save(Ident ident);

    List<Ident> findByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatus(LocalDate from, LocalDate to, Identtype type, Rekvireringsstatus rekvireringsstatus);

    long countByRekvireringsstatusAndIdenttype(Rekvireringsstatus rekvireringsstatus, Identtype identtype);

    @Modifying
    void deleteAll();

    @Query(value = "from Ident i where i.rekvireringsstatus = :rekvireringsstatus and i.foedselsdato > :foedtEtter")
    Page<Ident> findAll(@Param("rekvireringsstatus") Rekvireringsstatus rekvireringsstatus,
            @Param("foedtEtter") LocalDate foedtEtter, Pageable pageable);

    @Query(value = "from Ident i where i.rekvireringsstatus = :rekvireringsstatus and "
            + "(:kjoenn is null or (:kjoenn is not null and i.kjoenn = :kjoenn)) and "
            + "(:identtype is null or (:identtype is not null and i.identtype = :identtype)) and "
            + "i.foedselsdato between :foedtEtter and :foedtFoer and "
            + "i.syntetisk = :syntetisk")
    Page<Ident> findAll(@Param("rekvireringsstatus") Rekvireringsstatus rekvireringsstatus,
            @Param("identtype") Identtype identtype, @Param("kjoenn") Kjoenn kjoenn,
            @Param("foedtFoer") LocalDate foedtFoer, @Param("foedtEtter") LocalDate foedtEtter,
            @Param("syntetisk") boolean syntetisk, Pageable pageable);
}
