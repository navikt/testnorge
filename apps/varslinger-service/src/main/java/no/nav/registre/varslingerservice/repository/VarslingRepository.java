package no.nav.registre.varslingerservice.repository;

import org.springframework.data.repository.CrudRepository;

import no.nav.registre.varslingerservice.repository.model.VarslingModel;

public interface VarslingRepository extends CrudRepository<VarslingModel, String> {
}
