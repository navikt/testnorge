package no.nav.registre.varslingerapi.repository;

import org.springframework.data.repository.CrudRepository;

import no.nav.registre.varslingerapi.repository.model.BrukerModel;

public interface BrukerRepository extends CrudRepository<BrukerModel, String> {
}
