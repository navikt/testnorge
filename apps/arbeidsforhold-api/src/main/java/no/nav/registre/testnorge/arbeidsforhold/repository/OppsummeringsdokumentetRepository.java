package no.nav.registre.testnorge.arbeidsforhold.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

import no.nav.registre.testnorge.arbeidsforhold.repository.model.OppsummeringsdokumentetModel;

public interface OppsummeringsdokumentetRepository extends PagingAndSortingRepository<OppsummeringsdokumentetModel, String> {

    @Query(value = "from OppsummeringsdokumentetModel o1 where o1.rapporteringsmaaned = ?1 AND o1.orgnummer = ?2 AND o1.miljo = ?3 AND o1.version = (select max(version) from OppsummeringsdokumentetModel o2 where o2.rapporteringsmaaned = ?1 AND o2.orgnummer = ?2 AND o2.miljo = ?3)")
    Optional<OppsummeringsdokumentetModel> findBy(String rapporteringsmaaned, String orgnummer, String miljo);

    @Query(value = "from OppsummeringsdokumentetModel o1 where o1.miljo = ?1 AND o1.version = (select max(version) from OppsummeringsdokumentetModel o2 where o2.rapporteringsmaaned = o1.rapporteringsmaaned AND o2.orgnummer = o1.orgnummer AND o2.miljo = o1.miljo)")
    List<OppsummeringsdokumentetModel> findAllByLast(String miljo);

    @Query(value = "from OppsummeringsdokumentetModel o1 where o1.miljo = ?1 AND o1.version = (select max(version) from OppsummeringsdokumentetModel o2 where o2.rapporteringsmaaned = o1.rapporteringsmaaned AND o2.orgnummer = o1.orgnummer AND o2.miljo = o1.miljo)")
    Page<OppsummeringsdokumentetModel> findAllByLast(String miljo, Pageable pageable);

}