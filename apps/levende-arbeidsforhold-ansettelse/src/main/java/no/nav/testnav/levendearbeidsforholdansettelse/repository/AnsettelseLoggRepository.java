package no.nav.testnav.levendearbeidsforholdansettelse.repository;

import no.nav.testnav.levendearbeidsforholdansettelse.entity.AnsettelseLogg;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface AnsettelseLoggRepository extends ReactiveCrudRepository<AnsettelseLogg, Long> {

    Flux<AnsettelseLogg> findByFolkeregisterident(String ident);
    Flux<AnsettelseLogg> findByOrganisasjonsnummer(String orgnummer);
}
