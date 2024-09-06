package no.nav.testnav.levendearbeidsforholdansettelse.repository;

import no.nav.testnav.levendearbeidsforholdansettelse.entity.AnsettelseLogg;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface LoggRepository extends PagingAndSortingRepository<AnsettelseLogg, Long> {
}
