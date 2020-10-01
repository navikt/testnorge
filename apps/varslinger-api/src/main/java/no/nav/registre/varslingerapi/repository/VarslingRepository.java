package no.nav.registre.varslingerapi.repository;

import org.springframework.data.repository.CrudRepository;

import no.nav.registre.varslingerapi.repository.model.VarslingModel;

public interface VarslingRepository extends CrudRepository<VarslingModel, String> {
}
