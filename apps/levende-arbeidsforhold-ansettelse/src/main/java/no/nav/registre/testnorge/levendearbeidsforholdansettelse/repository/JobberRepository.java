package no.nav.registre.testnorge.levendearbeidsforholdansettelse.repository;

import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobberRepository extends JpaRepository<JobbParameter, Long> {

    JobbParameter save(JobbParameter jobbparam);
}
