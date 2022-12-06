package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.InfoStripe;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

public interface InformasjonsmeldingRepository extends CrudRepository<InfoStripe, Long> {

    @Query(value = "SELECT i FROM InfoStripe i WHERE " +
            " (i.start IS NULL OR i.start <= CURRENT_TIMESTAMP) AND " +
            " (i.expires IS NULL OR i.expires >= CURRENT_TIMESTAMP) " +
            "ORDER BY i.id DESC")
    Collection<InfoStripe> findGyldigMeldinger();
}
