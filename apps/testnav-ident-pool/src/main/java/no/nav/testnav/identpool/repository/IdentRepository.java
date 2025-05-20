package no.nav.testnav.identpool.repository;

import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Kjoenn;
import no.nav.testnav.identpool.domain.Rekvireringsstatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface IdentRepository extends R2dbcRepository<Ident, Long> {

    Mono<Boolean> existsByPersonidentifikator(String identifikator);

    Mono<Long> countByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatusAndSyntetisk(LocalDate from,
                                                                                 LocalDate to,
                                                                                 Identtype type,
                                                                                 Rekvireringsstatus rekvireringsstatus,
                                                                                 Boolean syntetisk);

    Mono<Ident> findByPersonidentifikator(String personidentifkator);

    Flux<Ident> findByPersonidentifikatorIn(List<String> personidentifikator);

    Flux<Ident> findByPersonidentifikatorIn(Set<String> idents);

    @Modifying
    Mono<Ident> save(Ident ident);

    Flux<Ident> findByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatusAndSyntetisk(LocalDate from,
                                                                                       LocalDate to,
                                                                                       Identtype type,
                                                                                       Rekvireringsstatus rekvireringsstatus,
                                                                                       Boolean syntetisk);

    Mono<Long> countByRekvireringsstatusAndIdenttype(Rekvireringsstatus rekvireringsstatus, Identtype identtype);

    @Query(value = "from Ident i where i.rekvireringsstatus = :rekvireringsstatus and i.foedselsdato > :foedtEtter")
    Mono<Page<Ident>> findAll(@Param("rekvireringsstatus") Rekvireringsstatus rekvireringsstatus,
                        @Param("foedtEtter") LocalDate foedtEtter, Pageable pageable);

    @Query(value = "from Ident i where i.rekvireringsstatus = :rekvireringsstatus and "
            + "(:kjoenn is null or (:kjoenn is not null and i.kjoenn = :kjoenn)) and "
            + "(:identtype is null or (:identtype is not null and i.identtype = :identtype)) and "
            + "i.foedselsdato between :foedtEtter and :foedtFoer and "
            + "i.syntetisk = :syntetisk")
    Mono<Page<Ident>> findAll(@Param("rekvireringsstatus") Rekvireringsstatus rekvireringsstatus,
                        @Param("identtype") Identtype identtype, @Param("kjoenn") Kjoenn kjoenn,
                        @Param("foedtFoer") LocalDate foedtFoer, @Param("foedtEtter") LocalDate foedtEtter,
                        @Param("syntetisk") boolean syntetisk, Pageable pageable);

    Mono<Integer> countAllByRekvireringsstatusAndIdenttypeAndSyntetiskAndFoedselsdatoBetween(
            Rekvireringsstatus rekvireringsstatus,
            Identtype identtype,
            Boolean syntetisk,
            LocalDate foedtEtter,
            LocalDate foedtFoer);

    Mono<Integer> countAllByRekvireringsstatusAndIdenttypeAndSyntetiskAndKjoennAndFoedselsdatoBetween(
            Rekvireringsstatus rekvireringsstatus,
            Identtype identtype,
            Boolean syntetisk,
            Kjoenn kjoenn,
            LocalDate foedtEtter,
            LocalDate foedtFoer);


    Mono<Page<Ident>> findAllByRekvireringsstatusAndIdenttypeAndSyntetiskAndFoedselsdatoBetween(
            Rekvireringsstatus rekvireringsstatus,
            Identtype identtype,
            Boolean syntetisk,
            LocalDate foedtEtter,
            LocalDate foedtFoer,
            Pageable pageable);

    Mono<Page<Ident>> findAllByRekvireringsstatusAndIdenttypeAndSyntetiskAndKjoennAndFoedselsdatoBetween(
            Rekvireringsstatus rekvireringsstatus,
            Identtype identtype,
            Boolean syntetisk,
            Kjoenn kjoenn,
            LocalDate foedtEtter,
            LocalDate foedtFoer,
            Pageable pageable);
}
