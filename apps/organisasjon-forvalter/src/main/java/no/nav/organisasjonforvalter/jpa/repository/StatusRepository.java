package no.nav.organisasjonforvalter.jpa.repository;

import no.nav.organisasjonforvalter.jpa.entity.Status;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StatusRepository extends CrudRepository<Status, Long> {

    @Query(value = "select s from Status s " +
            "where s.id in (select max(u.id) from Status u " +
            "where u.organisasjonsnummer in (:orgnr) " +
            "group by u.organisasjonsnummer, u.miljoe)")
    List<Status> findAllByOrganisasjonsnummerIn(@Param("orgnr") List<String> organisasjonsnummer);
}
