package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Soek;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SoekRepository extends CrudRepository<Soek, Long> {

    List<Soek> findByBrukerAndSoekType(Bruker bruker, Soek.SoekType soekType);
}