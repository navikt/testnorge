package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.BestillingMal;
import no.nav.dolly.domain.projection.MalBestilling;
import no.nav.dolly.domain.projection.MalBestillingFragment;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BestillingMalRepository extends ReactiveCrudRepository<BestillingMal, Long> {

    @Modifying
    @Query("update bestilling_mal b set mal_navn = :malNavn where b.id = :id")
    Mono<Integer> updateMalNavnById(@Param("id") Long id, @Param("malNavn") String malNavn);

    Flux<BestillingMal> findByBrukerIdAndMalNavn(Long brukerId, String navn);

    Flux<BestillingMal> findByBrukerId(Long brukerId);


    @Query(value = """
            select distinct bm.id, bm.mal_navn malnavn, bm.best_kriterier malbestilling, bm.miljoer,
                               bm.sist_oppdatert sistoppdatert
                        from bestilling_mal bm
            join bruker b on bm.bruker_id = b.id
            where (b.brukertype = 'AZURE' or b.brukertype = 'TEAM')
            order by bm.mal_navn;
            """)
    Flux<MalBestilling> findAllByBrukerAzureOrTeam();

    @Query(value = """
            select distinct bm.id, bm.mal_navn malnavn, bm.best_kriterier malbestilling, bm.miljoer,
                               bm.sist_oppdatert sistoppdatert
                        from bestilling_mal bm
            join bruker b on bm.bruker_id = b.id
            where b.bruker_id = :brukerId
            order by bm.mal_navn;
            """)
    Flux<MalBestilling> findAllByBrukerId(@Param("brukerId") String brukerId);

    @Query(value = """
            select distinct bm.id, bm.mal_navn malnavn, bm.best_kriterier malbestilling, bm.miljoer,
                               bm.sist_oppdatert sistoppdatert
                        from bestilling_mal bm
            where bm.bruker_id is null
            order by bm.mal_navn;
            """)
    Flux<MalBestilling> findAllByBrukerIsNull();

    @Query(value = """
            select distinct(b.brukernavn) as brukernavn, b.bruker_id as brukerid from bruker b
                join bestilling_mal bm on bm.bruker_id = b.id
                where (b.brukertype = 'AZURE' or b.brukertype = 'TEAM')
                order by brukernavn
            """)
    Flux<MalBestillingFragment> findAllByBrukertypeAzureOrTeam();

    @Query(value = """
            select b.brukernavn as brukernavn, b.bruker_id as brukerid from bruker b
                join bestilling_mal bm on bm.bruker_id = b.id
                where b.bruker_id = :brukerId
            """)
    Mono<MalBestillingFragment> findFragmentByBrukerId(@Param("brukerId") String brukerId);
}