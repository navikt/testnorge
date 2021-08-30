package no.nav.registre.varslingerservice.repository;

import org.springframework.data.repository.CrudRepository;

import no.nav.registre.varslingerservice.repository.model.BrukerModel;

public interface BrukerRepository extends CrudRepository<BrukerModel, String> {
}
