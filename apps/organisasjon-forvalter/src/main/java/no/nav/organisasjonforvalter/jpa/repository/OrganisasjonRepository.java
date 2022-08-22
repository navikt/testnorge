package no.nav.organisasjonforvalter.jpa.repository;

import no.nav.organisasjonforvalter.jpa.entity.Organisasjon;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrganisasjonRepository extends CrudRepository<Organisasjon, Long> {

    List<Organisasjon> findAllByOrganisasjonsnummerIn(List<String> orgnumre);

    @Query(value = "select o from Organisasjon as o " +
            "where o.brukerId = :brukerId " +
            "and o.enhetstype in ('BEDR', 'AAFY')")
    List<Organisasjon> findDriftsenheterByBrukerId(@Param(value = "brukerId") String brukerid);

    @Query(value = "select o from Organisasjon as o " +
            "where o.brukerId = :brukerId ")
    List<Organisasjon> findOrganisasjonerByBrukerId(@Param(value = "brukerId") String brukerid);

    Optional<Organisasjon> findByOrganisasjonsnummer(String orgnr);
}
