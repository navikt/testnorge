package no.nav.registre.sdforvalter.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import no.nav.registre.sdforvalter.database.model.AaregModel;
import no.nav.registre.sdforvalter.database.model.GruppeModel;

@Repository
public interface AaregRepository extends CrudRepository<AaregModel, String> {
    List<AaregModel> findByGruppeModel(GruppeModel gruppeModel);
}
