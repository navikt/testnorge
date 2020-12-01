package no.nav.organisasjonforvalter.jpa.repository;

import no.nav.organisasjonforvalter.jpa.entity.Organisasjon;
import org.springframework.data.repository.CrudRepository;

public interface OrganisasjonRepository extends CrudRepository<Organisasjon, Long> {
}
