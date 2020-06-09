package no.nav.registre.aaregstub.repository;

import org.springframework.data.repository.CrudRepository;

import no.nav.registre.aaregstub.arbeidsforhold.contents.Permisjon;

public interface PermisjonRepository extends CrudRepository<Permisjon, Long> {

}
