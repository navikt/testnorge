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

import java.util.List;
import java.util.Optional;

public interface TestgruppeRepository extends PagingAndSortingRepository<Testgruppe, Long> {

    Optional<Testgruppe> findById(Long id);

    Optional<TestgruppeUtenIdenter> findByIdOrderById(Long id);

    Testgruppe save(Testgruppe testgruppe);

    Page<Testgruppe> findAllByOpprettetAv(Bruker brukere, Pageable pageable);

    Page<Testgruppe> findAllByOrderByIdDesc(Pageable pageable);

    @Modifying
    @Query(value = "delete from Testgruppe tg where tg.id = :testgruppeId")
    int deleteAllById(@Param("testgruppeId") Long id);

    @Query(value = "select tg.* from gruppe tg " +
            "join bruker b on tg.opprettet_av = b.id " +
            "and b.bruker_Id in  (:brukere) ", nativeQuery = true)
    Page<Testgruppe> findAllByOpprettetAv_BrukerIdIn(@Param("brukere") List<String> brukere, PageRequest id);

    @Query(value = "select tg.id from gruppe tg " +
            "join bruker b on tg.opprettet_av = b.id " +
            "and b.bruker_Id in  (:brukere) ", nativeQuery = true)
    List<Long> findAllByOpprettetAv_BrukerIdIn(@Param("brukere") List<String> brukere);
}
