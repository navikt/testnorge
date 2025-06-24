package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Soek;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface SoekRepository extends PagingAndSortingRepository<Soek, Long> {

    List<Soek> findByBrukerAndSoekTypeOrderByIdDesc(Bruker bruker, Soek.SoekType soekType);

    @Modifying
    void delete(Soek soek);

    Soek save(Soek soek);
}