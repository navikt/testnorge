package no.nav.testnav.apps.tpservice.database.repository;

import no.nav.testnav.apps.tpservice.database.models.TKYtelseT;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TKYtelseTRepository extends CrudRepository<TKYtelseT, Integer> {

}
