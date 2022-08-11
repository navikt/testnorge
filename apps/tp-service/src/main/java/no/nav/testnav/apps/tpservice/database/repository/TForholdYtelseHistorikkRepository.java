package no.nav.testnav.apps.tpservice.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import no.nav.testnav.apps.tpservice.database.models.TForholdYtelseHistorikk;

@Repository
public interface TForholdYtelseHistorikkRepository extends CrudRepository<TForholdYtelseHistorikk, Integer> {

}
