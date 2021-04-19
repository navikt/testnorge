package no.nav.registre.testnav.genererorganisasjonpopulasjonservice.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import no.nav.registre.testnav.genererorganisasjonpopulasjonservice.repository.model.OpplysningspliktigModel;

@Repository
public interface OpplysningspliktigRepository extends CrudRepository<OpplysningspliktigModel, String> {
    List<OpplysningspliktigModel> findAllByMiljo(String miljo);
}
