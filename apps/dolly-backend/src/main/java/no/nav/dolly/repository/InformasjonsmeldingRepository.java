package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.InfoStripe;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;

public interface InformasjonsmeldingRepository extends CrudRepository<InfoStripe, Long> {

    Iterable<InfoStripe> findAllByExpiresIsNullOrExpiresIsAfter(LocalDateTime expiresAfter);

}
