package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingMal;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrganisasjonBestillingMalRepository extends CrudRepository<OrganisasjonBestillingMal, Long> {

    List<OrganisasjonBestillingMal> findByIdContaining(String id);

    List<OrganisasjonBestillingMal> findByBrukerAndMalBestillingNavn(Bruker bruker, String navn);

    List<OrganisasjonBestillingMal> findByBruker(Bruker bruker);

    @Query(value = "from OrganisasjonBestillingMal b where b.malBestillingNavn is not null order by b.malBestillingNavn")
    List<OrganisasjonBestillingMal> findMalBestilling();

    @Modifying
    @Query("update BestillingMal b set b.malNavn = :malNavn where b.id = :id")
    void updateMalBestillingNavnById(@Param("id") Long id, @Param("malNavn") String malNavn);

}
