package no.nav.registre.aaregstub.repository;

import io.micrometer.core.annotation.Timed;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;
import java.util.List;

import no.nav.registre.aaregstub.arbeidsforhold.contents.Arbeidsforhold;

public interface ArbeidsforholdRepository extends CrudRepository<Arbeidsforhold, Long> {

    @Timed(value = "aaregstub.resource.latency", extraTags = { "operation", "database" })
    @Query(nativeQuery = true, value = "select distinct(a.id) from arbeidsforhold a")
    List<BigInteger> getAllIds();
}
