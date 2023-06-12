package no.nav.registre.varslingerservice.repository;

import no.nav.registre.varslingerservice.repository.model.MottattVarslingModel;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MottattVarslingRepository extends CrudRepository<MottattVarslingModel, Long> {

    List<MottattVarslingModel> findAllByBrukerObjectId(String objectId);

}