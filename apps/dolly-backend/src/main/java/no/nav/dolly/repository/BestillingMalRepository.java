package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.BestillingMal;
import no.nav.dolly.domain.jpa.Bruker;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BestillingMalRepository extends CrudRepository<BestillingMal, Long> {
    @Transactional
    @Modifying
    @Query("update BestillingMal b set b.malBestillingNavn = ?2 where b.id = ?1")
    BestillingMal updateMalBestillingNavnById(Long id, String malBestillingNavn);

    List<BestillingMal> findByIdContaining(String id);

    List<BestillingMal> findByBrukerAndMalBestillingNavn(Bruker bruker, String navn);

    List<BestillingMal> findByBruker(Bruker bruker);

    @Query(value = "from BestillingMal b where b.malBestillingNavn is not null order by b.malBestillingNavn")
    List<BestillingMal> findMalBestilling();

}
