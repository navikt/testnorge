package no.nav.organisasjonforvalter.jpa.repository;

import no.nav.organisasjonforvalter.jpa.entity.Organisasjon;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface OrganisasjonRepository extends CrudRepository<Organisasjon, Long> {

    List<Organisasjon> findAllByOrganisasjonsnummerIn(List<String> orgnumre);

    List<Organisasjon> findAllByBrukerId(String brukerid);

    Optional<Organisasjon> findByOrganisasjonsnummer(String orgnr);
}
