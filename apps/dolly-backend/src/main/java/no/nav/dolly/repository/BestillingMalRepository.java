package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.BestillingMal;
import no.nav.dolly.domain.jpa.Bruker;
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
            select (b.brukernavn || ':' || b.bruker_id) malBruker from bruker b
                join bestilling_mal bm on b.id = bm.bruker_id
                and b.brukertype = 'AZURE'                                                  
                group by malBruker
                order by malBruker 
            """, nativeQuery = true)
    List<MalBestillingFragment> findAllByBrukertypeAzure();

    @Query(value = """
            select (b.brukernavn || ':' || b.bruker_id) malBruker from bruker b
                join bestilling_mal bm on b.id = bm.bruker_id
                and b.bruker_id in :brukerIds
                group by malBruker
                order by malBruker 
            """, nativeQuery = true)
    List<MalBestillingFragment> findAllByBrukerIdIn(@Param("brukerIds") List<String> brukerIds);
}
