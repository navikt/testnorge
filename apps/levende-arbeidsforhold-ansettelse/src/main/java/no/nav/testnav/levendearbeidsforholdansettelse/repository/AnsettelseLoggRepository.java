package no.nav.testnav.levendearbeidsforholdansettelse.repository;

import no.nav.testnav.levendearbeidsforholdansettelse.entity.AnsettelseLogg;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AnsettelseLoggRepository extends R2dbcRepository<AnsettelseLogg, Long> {

    Flux<AnsettelseLogg> findByFolkeregisterident(String ident);
    Flux<AnsettelseLogg> findByOrganisasjonsnummer(String orgnummer);

}
