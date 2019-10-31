package no.nav.registre.inntektsmeldingstub.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import no.nav.registre.inntektsmeldingstub.database.model.DelvisFravaer;

@Repository
public interface DelvisFravaerRepository extends CrudRepository<DelvisFravaer, Long> {
}
