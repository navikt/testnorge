package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingMal;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrganisasjonBestillingMalRepository extends CrudRepository<OrganisasjonBestillingMal, Long> {

    List<OrganisasjonBestillingMal> findByIdContaining(String id);

    List<OrganisasjonBestillingMal> findByBrukerAndMalBestillingNavn(Bruker bruker, String navn);

    List<OrganisasjonBestillingMal> findByBruker(Bruker bruker);

    @Query(value = "from OrganisasjonBestillingMal b where b.malBestillingNavn is not null order by b.malBestillingNavn")
    List<OrganisasjonBestillingMal> findMalBestilling();

    @Modifying
    @Query("update OrganisasjonBestillingMal b set b.malBestillingNavn = ?2 where b.id = ?1")
    void updateMalBestillingNavnById(Long id, String malBestillingNavn);

}
