package no.nav.registre.populasjoner.repository;

import org.springframework.data.repository.CrudRepository;

import no.nav.registre.populasjoner.domain.Ident;

public interface IdentRepository extends CrudRepository<Ident, Long> {

}
