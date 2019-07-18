package no.nav.registre.aaregstub.repository;

import io.micrometer.core.annotation.Timed;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

import no.nav.registre.aaregstub.arbeidsforhold.Ident;

public interface IdentRepository extends CrudRepository<Ident, String> {

    @Timed(value = "aaregstub.resource.latency", extraTags = { "operation", "database" })
    Optional<Ident> findByFnr(String fnr);

    @Timed(value = "aaregstub.resource.latency", extraTags = { "operation", "database" })
    @Query(nativeQuery = true, value = "select distinct(i.FNR) from ident i")
    List<String> getAllDistinctIdents();
}
