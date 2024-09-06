package no.nav.testnav.levendearbeidsforholdansettelse.repository;

import no.nav.testnav.levendearbeidsforholdansettelse.entity.JobbParameter;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ParameterRepository extends CrudRepository<JobbParameter, String> {

    List<JobbParameter> findAll();
}
