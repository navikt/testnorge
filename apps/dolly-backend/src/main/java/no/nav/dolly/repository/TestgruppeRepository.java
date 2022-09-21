package no.nav.dolly.repository;

import no.nav.dolly.domain.jpa.Testgruppe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TestgruppeRepository extends PagingAndSortingRepository<Testgruppe, Long> {

    Optional<Testgruppe> findById(Long id);

    List<Testgruppe> findAllById(Iterable<Long> ids);

    Testgruppe save(Testgruppe testgruppe);

    List<Testgruppe> findAllByOrderByNavn();

    Page<Testgruppe> findAllByOrderByIdDesc(Pageable pageable);

    int deleteTestgruppeById(Long id);

    @Query(value = "from Testgruppe g " +
            "join Bruker b on b.id = g.opprettet_av " +
            "and nav_ident = :navIdent " +
            "and eid_av_id is null " +
            "and migrert is null")
    List<Testgruppe> getTestgrupperByNavIdent(@Param("navIdent") String navIdent);
}
