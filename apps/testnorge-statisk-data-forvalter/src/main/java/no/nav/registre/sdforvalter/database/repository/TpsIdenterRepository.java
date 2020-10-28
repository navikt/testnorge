package no.nav.registre.sdforvalter.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import no.nav.registre.sdforvalter.database.model.GruppeModel;
import no.nav.registre.sdforvalter.database.model.TpsIdentModel;

@Repository
public interface TpsIdenterRepository extends CrudRepository<TpsIdentModel, String> {
    List<TpsIdentModel> findByGruppeModel(GruppeModel gruppeModel);

    TpsIdentModel findByFnr(String fnr);
}
