package no.nav.testnav.apps.tpservice.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import no.nav.testnav.apps.tpservice.database.models.TKMeldingT;

@Repository
public interface TKMeldingTRepository extends CrudRepository<TKMeldingT, Integer> {

}
