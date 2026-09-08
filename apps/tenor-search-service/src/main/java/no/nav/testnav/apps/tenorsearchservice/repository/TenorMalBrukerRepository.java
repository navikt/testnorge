package no.nav.testnav.apps.tenorsearchservice.repository;

import no.nav.testnav.apps.tenorsearchservice.domain.TenorMalBruker;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorMalBrukerType;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;

@Repository
public interface TenorMalBrukerRepository extends ReactiveSortingRepository<TenorMalBruker, Long> {

    Mono<Void> deleteAll();

    @Query("""
            insert into tenor_mal_bruker (bruker_id, brukernavn, brukertype, sist_oppdatert)
            values (:brukerId, :brukernavn, :brukertype, current_timestamp)
            on conflict (bruker_id) do update
            set brukernavn = excluded.brukernavn,
                brukertype = excluded.brukertype,
                sist_oppdatert = current_timestamp
            returning *
            """)
    Mono<TenorMalBruker> upsert(
            @Param("brukerId") String brukerId,
            @Param("brukernavn") String brukernavn,
            @Param("brukertype") String brukertype);

    @Query("""
            select distinct b.*
            from tenor_mal_bruker b
            join tenor_mal m on m.bruker_id = b.id
            where b.brukertype = :brukertype
            order by lower(b.brukernavn), b.bruker_id
            """)
    Flux<TenorMalBruker> findBrukereMedMalerByBrukertype(
            @Param("brukertype") TenorMalBrukerType brukertype);

    @Query("""
            select distinct b.*
            from tenor_mal_bruker b
            join tenor_mal m on m.bruker_id = b.id
            where b.brukertype = 'BANKID'
              and b.bruker_id in (:brukerIds)
            order by lower(b.brukernavn), b.bruker_id
            """)
    Flux<TenorMalBruker> findBankIdBrukereMedMalerByBrukerIdIn(
            @Param("brukerIds") Collection<String> brukerIds);
}
