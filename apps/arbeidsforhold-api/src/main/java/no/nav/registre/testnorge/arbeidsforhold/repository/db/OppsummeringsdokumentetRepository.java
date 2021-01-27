package no.nav.registre.testnorge.arbeidsforhold.repository.db;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

import no.nav.registre.testnorge.arbeidsforhold.repository.db.model.OppsummeringsdokumentetModel;

public interface OppsummeringsdokumentetRepository extends PagingAndSortingRepository<OppsummeringsdokumentetModel, Long> {

    @Query(value = "from OppsummeringsdokumentetModel o1 where o1.year = ?1 AND o1.month = ?2 AND o1.orgnummer = ?3 AND o1.miljo = ?4 AND o1.version = (select max(version) from OppsummeringsdokumentetModel o2 where o2.year = ?1 AND o2.month = ?2 AND o2.orgnummer = ?3 AND o2.miljo = ?4)")
    Optional<OppsummeringsdokumentetModel> findBy(Integer year, Integer month, String orgnummer, String miljo);

    @Query(value = "from OppsummeringsdokumentetModel o1 where o1.miljo = ?1 AND o1.version = (select max(version) from OppsummeringsdokumentetModel o2 where o2.year = o1.year AND o2.month = o1.month AND o2.orgnummer = o1.orgnummer AND o2.miljo = o1.miljo)")
    List<OppsummeringsdokumentetModel> findAllByLast(String miljo);

    @Query(value = "from OppsummeringsdokumentetModel o1 where o1.miljo = ?1 AND o1.version = (select max(version) from OppsummeringsdokumentetModel o2 where o2.year = o1.year AND o2.month = o1.month AND o2.orgnummer = o1.orgnummer AND o2.miljo = o1.miljo) ORDER BY o1.createdAt ASC")
    Page<OppsummeringsdokumentetModel> findAllByLast(String miljo, Pageable pageable);

    @Query(value = "from OppsummeringsdokumentetModel o1 where o1.miljo = ?1 AND (o1.year > ?2 OR o1.year = ?2 AND o1.month >= ?3) AND (o1.year < ?4 OR o1.year = ?4 AND o1.month <= ?5) AND o1.version = (select max(version) from OppsummeringsdokumentetModel o2 where o2.year = o1.year AND o2.month = o1.month AND o2.orgnummer = o1.orgnummer AND o2.miljo = o1.miljo) ORDER BY o1.createdAt ASC")
    Page<OppsummeringsdokumentetModel> findAllByLastWithFomAndTom(String miljo, int fomYear, int fomMonth, int tomYear, int tomMonth, Pageable pageable);

    @Query(value = "from OppsummeringsdokumentetModel o1 where o1.miljo = ?1 AND (o1.year > ?2 OR o1.year = ?2 AND o1.month >= ?3) AND o1.version = (select max(version) from OppsummeringsdokumentetModel o2 where o2.year = o1.year AND o2.month = o1.month AND o2.orgnummer = o1.orgnummer AND o2.miljo = o1.miljo) ORDER BY o1.createdAt ASC")
    Page<OppsummeringsdokumentetModel> findAllByLastWithFom(String miljo, int fomYear, int fomMonth, Pageable pageable);

    @Query(value = "from OppsummeringsdokumentetModel o1 where o1.miljo = ?1 AND (o1.year < ?2 OR o1.year = ?2 AND o1.month <= ?3) AND o1.version = (select max(version) from OppsummeringsdokumentetModel o2 where o2.year = o1.year AND o2.month = o1.month AND o2.orgnummer = o1.orgnummer AND o2.miljo = o1.miljo) ORDER BY o1.createdAt ASC")
    Page<OppsummeringsdokumentetModel> findAllByLastWithTom(String miljo, int tomYear, int tomMonth, Pageable pageable);
}