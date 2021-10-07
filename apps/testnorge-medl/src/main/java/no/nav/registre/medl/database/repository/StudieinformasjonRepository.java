package no.nav.registre.medl.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import no.nav.registre.medl.database.model.TStudieinformasjon;

@Repository
public interface StudieinformasjonRepository extends CrudRepository<TStudieinformasjon, Long> {

}
