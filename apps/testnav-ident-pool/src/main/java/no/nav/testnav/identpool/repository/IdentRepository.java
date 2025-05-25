package no.nav.testnav.identpool.repository;

import no.nav.testnav.identpool.domain.Ident;
import no.nav.testnav.identpool.domain.Identtype;
import no.nav.testnav.identpool.domain.Kjoenn;
import no.nav.testnav.identpool.domain.Rekvireringsstatus;
import no.nav.testnav.identpool.dto.ExistsDato;
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

    @Query(value = """
            select count(i), i.foedslsdato from Ident i
            where i.foedselsdato between :from and :to
            and i.identtype = :identtype
            and i.rekvireringsstatus = :rekvireringsstatus
            and i.syntetisk = :syntetisk
            group by i.foedslsdato
            order by i.foedslsdato
            """)
    Flux<ExistsDato> countByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatusAndSyntetisk(LocalDate from,
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

    @Query(value = """
            from Ident i
                        where i.rekvireringsstatus = :rekvireringsstatus
                        and i.foedselsdato > :foedtEtter
                        and i.syntetisk = false
            """)
    Flux<Ident> findAllIkkeSyntetisk(@Param("rekvireringsstatus") Rekvireringsstatus rekvireringsstatus,
                                     @Param("foedtEtter") LocalDate foedtEtter, Pageable pageable);

    @Query(value = """
            select count(i) from Ident i
                        where i.rekvireringsstatus = :rekvireringsstatus
                        and i.foedselsdato > :foedtEtter
                        and i.syntetisk = false
            """)
    Mono<Integer> countAllIkkeSyntetisk(@Param("rekvireringsstatus") Rekvireringsstatus rekvireringsstatus,
                                        @Param("foedtEtter") LocalDate foedtEtter);

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
