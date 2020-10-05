package no.nav.dolly.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.repository.Repository;

import no.nav.dolly.domain.jpa.Testgruppe;

public interface TestgruppeRepository extends Repository<Testgruppe, Long> {

    Optional<Testgruppe> findById(Long id);

    List<Testgruppe> findAllById(Iterable<Long> ids);

    Testgruppe save(Testgruppe testgruppe);

    List<Testgruppe> saveAll(Iterable<Testgruppe> testgrupper);

    Set<Testgruppe> findAllByOrderByNavn();

    int deleteTestgruppeById(Long id);
}
