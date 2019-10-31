package no.nav.registre.inntektsmeldingstub.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import no.nav.registre.inntektsmeldingstub.database.model.Arbeidsforhold;

@Repository
public interface ArbeidsforholdRepository extends CrudRepository<Arbeidsforhold, Long> {
    Optional<Arbeidsforhold> findByArbeidforholdsId(String arbeidforholdsId);
}
