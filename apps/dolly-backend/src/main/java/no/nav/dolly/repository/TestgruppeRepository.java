package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.projection.TestgruppeUtenIdenter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
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

    Page<Testgruppe> findAllByOpprettetAvIn(Collection<Bruker> brukere, Pageable pageable);

    Page<Testgruppe> findAllByOrderByIdDesc(Pageable pageable);

    @Modifying
    @Query(value = "delete from Testgruppe tg where tg.id = :testgruppeId")
    int deleteAllById(@Param("testgruppeId") Long id);

    @Query(value = "select tg from Testgruppe tg " +
            "join Bruker b on tg.opprettetAv = b " +
            "and b.id in (:brukere)")
    Page<Testgruppe> findAllByOpprettetAv_BrukerIdIn(@Param("brukere") List<String> brukere, PageRequest id);
}
