package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.projection.TestgruppeUtenIdenter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TestgruppeRepository extends PagingAndSortingRepository<Testgruppe, Long> {

    Optional<Testgruppe> findById(Long id);

    Optional<TestgruppeUtenIdenter> findByIdOrderById(Long id);

    List<Testgruppe> findAllById(Iterable<Long> ids);

    Testgruppe save(Testgruppe testgruppe);

    Page<Testgruppe> findAllByOrderByNavn(Pageable pageable);

    Page<Testgruppe> findAllByOpprettetAvIn(Collection<Bruker> brukere, Pageable pageable);

    Page<Testgruppe> findAllByOrderByIdDesc(Pageable pageable);

    int deleteTestgruppeById(Long id);

    @Query(value = "from Testgruppe g " +
            "join Bruker b on b.id = g.opprettetAv " +
            "and b.navIdent = :navId " +
            "and b.eidAv is null " +
            "and b.migrert is null")
    List<Testgruppe> getIkkemigrerteTestgrupperByNavId(@Param("navId") String navId);
}
