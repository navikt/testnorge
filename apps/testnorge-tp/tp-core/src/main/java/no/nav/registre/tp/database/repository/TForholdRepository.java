package no.nav.registre.tp.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import no.nav.registre.tp.database.models.TForhold;

@Repository
public interface TForholdRepository extends CrudRepository<TForhold, Integer> {

    TForhold findByPersonId(Integer personId);
}
