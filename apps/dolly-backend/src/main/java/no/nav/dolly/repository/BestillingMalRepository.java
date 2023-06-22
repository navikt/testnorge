package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.BestillingMal;
import no.nav.dolly.domain.jpa.Bruker;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BestillingMalRepository extends CrudRepository<BestillingMal, Long> {
    @Modifying
    @Query("update BestillingMal b set b.malNavn = :malNavn where b.id = :id")
    void updateMalBestillingNavnById(@Param("id") Long id, @Param("malNavn") String malNavn);

    List<BestillingMal> findByIdContaining(String id);

    List<BestillingMal> findByBrukerAndMalBestillingNavn(Bruker bruker, String navn);

    List<BestillingMal> findByBruker(Bruker bruker);

    List<BestillingMal> findMalBestilling();

}
