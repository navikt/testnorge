package no.nav.dolly.regression.testrepositories;

import no.nav.dolly.domain.jpa.Testgruppe;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GruppeTestRepository extends CrudRepository<Testgruppe, Long> {

    void deleteAll();

    List<Testgruppe> findAllByOrderByNavn();

    Testgruppe findByNavn(String mingruppe);
}
