package no.nav.registre.populasjoner.repository;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

import no.nav.registre.populasjoner.domain.Ident;

public interface IdentRepository extends CrudRepository<Ident, Long> {

    Optional<Ident> findByFnr(String fnr);
}
