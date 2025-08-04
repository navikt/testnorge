package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.BestillingMal;
import no.nav.dolly.domain.resultset.entity.bestilling.MalBestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.MalBestillingFragment;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BestillingMalRepository extends ReactiveCrudRepository<BestillingMal, Long> {

    @Modifying
    @Query("update bestilling_mal b set mal_navn = :malNavn where b.id = :id")
    Mono<BestillingMal> updateMalNavnById(@Param("id") Long id, @Param("malNavn") String malNavn);

    Flux<BestillingMal> findByBrukerIdAndMalNavn(Long brukerId, String navn);

    Flux<BestillingMal> findByBrukerId(Long brukerId);

    @Query(value = """
            select bm.id, bm.mal_navn malNavn, bm.best_kriterier malBestilling, bm.miljoer,
                               bm.sist_oppdatert sistOppdatert
                        from bestilling_mal bm
            join bruker b on bm.bruker_id = b.id
            and b.brukertype = 'AZURE'
            order by bm.mal_navn;
            """)
    Flux<MalBestilling> findAllByBrukerAzure();

    @Query(value = """
            select bm.id, bm.mal_navn malNavn, bm.best_kriterier malBestilling, bm.miljoer,
                               bm.sist_oppdatert sistOppdatert
                        from bestilling_mal bm
            join bruker b on bm.bruker_id = b.id
            where b.bruker_id = :brukerId
            order by bm.mal_navn;
            """)
    Flux<MalBestilling> findAllByBrukerId(@Param("brukerId") String brukerId);

    @Query(value = """
            select bm.id, bm.mal_navn malNavn, bm.best_kriterier malBestilling, bm.miljoer,
                               bm.sist_oppdatert sistOppdatert
                        from bestilling_mal bm
            where bm.bruker_id is null
            order by bm.mal_navn;
            """)
    Flux<MalBestilling> findAllByBrukerIsNull();

    @Query(value = """
            select (b.brukernavn || ':' || b.bruker_id) malBruker from bruker b
                join bestilling_mal bm on b.id = bm.bruker_id
                and b.brukertype = 'AZURE'
                group by malBruker
                order by malBruker
            """)
    Flux<MalBestillingFragment> findAllByBrukertypeAzure();

    @Query(value = """
            select (b.brukernavn || ':' || b.bruker_id) malBruker from bruker b
                join bestilling_mal bm on b.id = bm.bruker_id
                and b.bruker_id in :brukerIds
                group by malBruker
                order by malBruker
            """)
    Flux<MalBestillingFragment> findAllByBrukerIdIn(@Param("brukerIds") List<String> brukerIds);
}
