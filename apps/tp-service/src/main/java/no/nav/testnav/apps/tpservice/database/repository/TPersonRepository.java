package no.nav.testnav.apps.tpservice.database.repository;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

import no.nav.testnav.apps.tpservice.database.models.TPerson;

public interface TPersonRepository extends CrudRepository<TPerson, Integer> {

    TPerson findByFnrFk(String fnr);

    List<TPerson> findAllByFnrFkIn(List<String> fnr);
}
