package no.nav.registre.testnorge.levendearbeidsforholdansettelse.repository;

import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.AnsettelseLogg;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AnsettelseLoggRepository extends CrudRepository<AnsettelseLogg, Long> {

    List<AnsettelseLogg> findByFolkeregisterident(String ident);
    List<AnsettelseLogg> findByOrganisasjonsnummer(String orgnummer);
}
