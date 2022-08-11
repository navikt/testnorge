package no.nav.testnav.apps.tpservice.database.repository;

import no.nav.testnav.apps.tpservice.database.models.TForhold;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TForholdRepository extends CrudRepository<TForhold, Integer> {

    TForhold findByPersonId(Integer personId);
}
