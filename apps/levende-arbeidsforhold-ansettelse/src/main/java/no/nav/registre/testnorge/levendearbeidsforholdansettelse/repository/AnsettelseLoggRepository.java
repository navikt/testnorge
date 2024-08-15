package no.nav.registre.testnorge.levendearbeidsforholdansettelse.repository;

import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.AnsettelseLogg;
import org.springframework.data.repository.CrudRepository;

public interface AnsettelseLoggRepository extends CrudRepository<AnsettelseLogg, Long> {
}
