package no.nav.registre.varslingerapi.repository;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

import no.nav.registre.varslingerapi.repository.model.MottattVarslingModel;

public interface MottattVarslingRepository extends CrudRepository<MottattVarslingModel, Long> {

    List<MottattVarslingModel> findAllByBrukerObjectId(String objectId);
}
