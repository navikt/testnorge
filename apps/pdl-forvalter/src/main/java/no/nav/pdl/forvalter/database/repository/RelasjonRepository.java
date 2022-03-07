package no.nav.pdl.forvalter.database.repository;

import no.nav.pdl.forvalter.database.model.DbRelasjon;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelasjonRepository extends CrudRepository<DbRelasjon, Long> {

    List<DbRelasjon> findByPersonIdent(String ident);
}
