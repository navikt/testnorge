package no.nav.testnav.identpool.repository;

import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Kjoenn;
import no.nav.testnav.identpool.domain.Rekvireringsstatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface IdentRepository extends JpaRepository<Ident, Long> {

    boolean existsByPersonidentifikator(String identifikator);

    long countByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatusAndSyntetisk(LocalDate from,
                                                                                 LocalDate to,
                                                                                 Identtype type,
                                                                                 Rekvireringsstatus rekvireringsstatus,
                                                                                 Boolean syntetisk);

    Ident findByPersonidentifikator(String personidentifkator);

    List<Ident> findByPersonidentifikatorIn(List<String> personidentifikator);

    Set<Ident> findByPersonidentifikatorIn(Set<String> idents);

    @Modifying
    Ident save(Ident ident);

    List<Ident> findByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatusAndSyntetisk(LocalDate from,
                                                                                       LocalDate to,
                                                                                       Identtype type,
                                                                                       Rekvireringsstatus rekvireringsstatus,
                                                                                       Boolean syntetisk);

    long countByRekvireringsstatusAndIdenttype(Rekvireringsstatus rekvireringsstatus, Identtype identtype);

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

    @Query
    int countAllByRekvireringsstatusAndIdenttypeAndSyntetiskAndFoedselsdatoBetween(
            Rekvireringsstatus rekvireringsstatus,
            Identtype identtype,
            Boolean syntetisk,
            LocalDate foedtEtter,
            LocalDate foedtFoer);

    @Query
    int countAllByRekvireringsstatusAndIdenttypeAndSyntetiskAndKjoennAndFoedselsdatoBetween(
            Rekvireringsstatus rekvireringsstatus,
            Identtype identtype,
            Boolean syntetisk,
            Kjoenn kjoenn,
            LocalDate foedtEtter,
            LocalDate foedtFoer);

    @Query
    Page<Ident> findAllByRekvireringsstatusAndIdenttypeAndSyntetiskAndFoedselsdatoBetween(
            Rekvireringsstatus rekvireringsstatus,
            Identtype identtype,
            Boolean syntetisk,
            LocalDate foedtEtter,
            LocalDate foedtFoer,
            Pageable pageable);

    @Query
    Page<Ident> findAllByRekvireringsstatusAndIdenttypeAndSyntetiskAndKjoennAndFoedselsdatoBetween(
            Rekvireringsstatus rekvireringsstatus,
            Identtype identtype,
            Boolean syntetisk,
            Kjoenn kjoenn,
            LocalDate foedtEtter,
            LocalDate foedtFoer,
            Pageable pageable);
}
