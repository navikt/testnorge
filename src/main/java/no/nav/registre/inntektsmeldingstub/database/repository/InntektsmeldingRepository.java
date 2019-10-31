package no.nav.registre.inntektsmeldingstub.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import no.nav.registre.inntektsmeldingstub.database.model.Inntektsmelding;

@Repository
public interface InntektsmeldingRepository extends CrudRepository<Inntektsmelding, Long> {
}
