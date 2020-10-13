package no.nav.registre.sdforvalter.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import no.nav.registre.sdforvalter.database.model.EregModel;
import no.nav.registre.sdforvalter.database.model.GruppeModel;

@Repository
public interface EregRepository extends CrudRepository<EregModel, String> {
    List<EregModel> findByGruppeModel(GruppeModel gruppeModel);
}
