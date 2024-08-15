package no.nav.registre.testnorge.levendearbeidsforholdansettelse.repository;

import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.AnsettelseLogg;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface LoggRepository extends PagingAndSortingRepository<AnsettelseLogg, Long> {
}
