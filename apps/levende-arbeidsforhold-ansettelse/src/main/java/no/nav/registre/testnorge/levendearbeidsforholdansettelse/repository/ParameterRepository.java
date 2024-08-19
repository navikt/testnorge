package no.nav.registre.testnorge.levendearbeidsforholdansettelse.repository;

import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameter;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ParameterRepository extends CrudRepository<JobbParameter, String> {

    List<JobbParameter> findAll();
}
