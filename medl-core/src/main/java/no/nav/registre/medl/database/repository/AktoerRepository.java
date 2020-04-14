package no.nav.registre.medl.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import no.nav.registre.medl.database.model.TAktoer;

@Repository
public interface AktoerRepository extends CrudRepository<TAktoer, Long> {

    List<TAktoer> findAllByIdentIn(List<String> identer);
}
