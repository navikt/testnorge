package no.nav.testnav.levendearbeidsforholdansettelse.repository;

import no.nav.testnav.levendearbeidsforholdansettelse.entity.JobbParameter;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ParameterRepository extends ReactiveCrudRepository<JobbParameter, String> {
}
