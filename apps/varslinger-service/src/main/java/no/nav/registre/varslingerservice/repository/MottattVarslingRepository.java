package no.nav.registre.varslingerservice.repository;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

import no.nav.registre.varslingerservice.repository.model.MottattVarslingModel;

public interface MottattVarslingRepository extends CrudRepository<MottattVarslingModel, Long> {

    List<MottattVarslingModel> findAllByBrukerObjectId(String objectId);

    void deleteAllByBrukerObjectId(String objectId);
}