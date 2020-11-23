package no.nav.dolly.repository.postgres;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import no.nav.dolly.domain.jpa.postgres.Testgruppe;

public interface TestgruppeRepository extends PagingAndSortingRepository<Testgruppe, Long> {

    Optional<Testgruppe> findById(Long id);

    List<Testgruppe> findAllByOrderById();

    List<Testgruppe> findAllById(Iterable<Long> ids);

    Testgruppe save(Testgruppe testgruppe);

    List<Testgruppe> findAllByOrderByNavn();

    Page<Testgruppe> findAllByOrderByNavn(Pageable pageable);

    int deleteTestgruppeById(Long id);
}
