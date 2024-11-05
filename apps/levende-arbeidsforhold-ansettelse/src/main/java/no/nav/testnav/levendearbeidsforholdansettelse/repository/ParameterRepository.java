package no.nav.testnav.levendearbeidsforholdansettelse.repository;

import no.nav.testnav.levendearbeidsforholdansettelse.entity.JobbParameter;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParameterRepository extends R2dbcRepository<JobbParameter, String> {
}
