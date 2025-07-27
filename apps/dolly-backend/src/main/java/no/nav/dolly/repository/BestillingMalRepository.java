package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.BestillingMal;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.resultset.entity.bestilling.MalBestilling;
import no.nav.dolly.domain.resultset.entity.bestilling.MalBestillingFragment;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BestillingMalRepository extends CrudRepository<BestillingMal, Long> {

    @Modifying
    @Query("update BestillingMal b set b.malNavn = :malNavn where b.id = :id")
    void updateMalNavnById(@Param("id") Long id, @Param("malNavn") String malNavn);

    List<BestillingMal> findByBrukerAndMalNavn(Bruker bruker, String navn);

    List<BestillingMal> findByBruker(Bruker bruker);


    @Query(value = """
            select distinct bm.id, bm.mal_navn malNavn, bm.best_kriterier malBestilling, bm.miljoer,
                               bm.sist_oppdatert sistOppdatert
                        from bestilling_mal bm
            join bruker b on bm.bruker_id = b.id
            where (b.brukertype = 'AZURE' or b.brukertype = 'TEAM')
            order by bm.mal_navn;
            """, nativeQuery = true)
    List<MalBestilling> findAllByBrukerAzureOrTeam();


    @Query(value = """
            select distinct bm.id, bm.mal_navn malNavn, bm.best_kriterier malBestilling, bm.miljoer,
                               bm.sist_oppdatert sistOppdatert
                        from bestilling_mal bm
            join bruker b on bm.bruker_id = b.id
            where b.bruker_id = :brukerId
            order by bm.mal_navn;
            """, nativeQuery = true)
    List<MalBestilling> findAllByBrukerId(@Param("brukerId") String brukerId);

    @Query(value = """
            select distinct bm.id, bm.mal_navn malNavn, bm.best_kriterier malBestilling, bm.miljoer,
                               bm.sist_oppdatert sistOppdatert
                        from bestilling_mal bm
            where bm.bruker_id is null
            order by bm.mal_navn;
            """, nativeQuery = true)
    List<MalBestilling> findAllByBrukerIsNull();

    @Query(value = """
            select distinct (b.brukernavn || ':' || b.bruker_id) malBruker from bruker b
                join bestilling_mal bm on b.id = bm.bruker_id
                where (b.brukertype = 'AZURE' or b.brukertype = 'TEAM')
                group by malBruker
                order by malBruker
            """, nativeQuery = true)
    List<MalBestillingFragment> findAllByBrukertypeAzureOrTeam();


    @Query(value = """
            select distinct (b.brukernavn || ':' || b.bruker_id) malBruker from bruker b
                join bestilling_mal bm on b.id = bm.bruker_id
                where b.bruker_id in :brukerIds
                group by malBruker
                order by malBruker
            """, nativeQuery = true)
    List<MalBestillingFragment> findAllByBrukerIdIn(@Param("brukerIds") List<String> brukerIds);
}