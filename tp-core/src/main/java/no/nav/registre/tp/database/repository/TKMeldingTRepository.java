package no.nav.registre.tp.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import no.nav.registre.tp.database.models.TKMeldingT;

@Repository
public interface TKMeldingTRepository extends CrudRepository<TKMeldingT, Integer> {

}
