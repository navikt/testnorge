package no.nav.testnav.identpool.repository;

import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Kjoenn;
import no.nav.testnav.identpool.domain.Rekvireringsstatus;
import org.springframework.data.domain.Pageable;
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

    Mono<Ident> findByPersonidentifikator(String personidentifkator);

    Flux<Ident> findByPersonidentifikatorIn(List<String> personidentifikator);

    Flux<Ident> findByPersonidentifikatorIn(Set<String> idents);

    Flux<Ident> findByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatusAndSyntetisk(LocalDate from,
                                                                                       LocalDate to,
                                                                                       Identtype identtype,
                                                                                       Rekvireringsstatus rekvireringsstatus,
                                                                                       Boolean syntetisk);

    Mono<Long> countByRekvireringsstatusAndIdenttype(Rekvireringsstatus rekvireringsstatus, Identtype identtype);

    @Query(value = """
            select * from Personidentifikator i
                        where i.rekvireringsstatus = :rekvireringsstatus
                        and i.foedselsdato between :foedtEtter and :foedtFoer
                        and i.syntetisk = false
            """)
    Flux<Ident> findAllIkkeSyntetisk(@Param("rekvireringsstatus") Rekvireringsstatus rekvireringsstatus,
                                     @Param("foedtEtter") LocalDate foedtEtter,
                                     @Param("foedtFoer") LocalDate foedtFoer);

    @Query(value = """
            select count(*) from Personidentifikator i
                        where i.rekvireringsstatus = :rekvireringsstatus
                        and i.foedselsdato between :foedtEtter and :foedtFoer
                        and i.syntetisk = false
            """)
    Mono<Integer> countAllIkkeSyntetisk(@Param("rekvireringsstatus") Rekvireringsstatus rekvireringsstatus,
                                        @Param("foedtEtter") LocalDate foedtEtter,
                                        @Param("foedtFoer") LocalDate foedtFoer);

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

    Flux<Ident> findAllByRekvireringsstatusAndIdenttypeAndSyntetiskAndFoedselsdatoBetween(
            Rekvireringsstatus rekvireringsstatus,
            Identtype identtype,
            Boolean syntetisk,
            LocalDate foedtEtter,
            LocalDate foedtFoer,
            Pageable pageable);

    Flux<Ident> findAllByRekvireringsstatusAndIdenttypeAndSyntetiskAndKjoennAndFoedselsdatoBetween(
            Rekvireringsstatus rekvireringsstatus,
            Identtype identtype,
            Boolean syntetisk,
            Kjoenn kjoenn,
            LocalDate foedtEtter,
            LocalDate foedtFoer,
            Pageable pageable);
}
