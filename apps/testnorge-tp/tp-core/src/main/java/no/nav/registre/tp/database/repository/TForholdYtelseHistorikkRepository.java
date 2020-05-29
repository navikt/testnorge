package no.nav.registre.tp.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import no.nav.registre.tp.database.models.TForholdYtelseHistorikk;

@Repository
public interface TForholdYtelseHistorikkRepository extends CrudRepository<TForholdYtelseHistorikk, Integer> {

}
