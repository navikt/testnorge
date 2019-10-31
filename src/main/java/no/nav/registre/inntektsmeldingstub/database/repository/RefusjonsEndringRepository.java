package no.nav.registre.inntektsmeldingstub.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import no.nav.registre.inntektsmeldingstub.database.model.RefusjonsEndring;

@Repository
public interface RefusjonsEndringRepository extends CrudRepository<RefusjonsEndring, Long> {
}
