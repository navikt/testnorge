package no.nav.registre.aaregstub.repository;

import org.springframework.data.repository.CrudRepository;

import no.nav.registre.aaregstub.arbeidsforhold.contents.Arbeidsavtale;

public interface ArbeidsavtaleRepository extends CrudRepository<Arbeidsavtale, Long> {

}
