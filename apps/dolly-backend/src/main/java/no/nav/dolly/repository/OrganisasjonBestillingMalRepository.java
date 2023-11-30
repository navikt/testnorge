package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.OrganisasjonBestillingMal;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrganisasjonBestillingMalRepository extends CrudRepository<OrganisasjonBestillingMal, Long> {

    List<OrganisasjonBestillingMal> findByBrukerAndMalNavn(Bruker bruker, String navn);

    List<OrganisasjonBestillingMal> findByBruker(Bruker bruker);

    @Modifying
    @Query("update OrganisasjonBestillingMal b set b.malNavn = :malNavn where b.id = :id")
    int updateMalNavnById(@Param("id") Long id, @Param("malNavn") String malNavn);

}
