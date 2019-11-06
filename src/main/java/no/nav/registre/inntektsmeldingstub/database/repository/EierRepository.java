package no.nav.registre.inntektsmeldingstub.database.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import no.nav.registre.inntektsmeldingstub.database.model.Eier;

@Repository
public interface EierRepository extends CrudRepository<Eier, Long> {

    Optional<Eier> findEierByNavn(String navn);
}
