package no.nav.registre.inntektsmeldingstub.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import no.nav.registre.inntektsmeldingstub.database.model.Arbeidsgiver;

@Repository
public interface ArbeidsgiverRepository extends CrudRepository<Arbeidsgiver, Integer> {
    Optional<Arbeidsgiver> findByVirksomhetsnummer(String virksomhetsnummer);
}
