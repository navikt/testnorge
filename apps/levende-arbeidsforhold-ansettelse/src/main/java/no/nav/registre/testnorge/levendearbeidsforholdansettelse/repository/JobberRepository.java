package no.nav.registre.testnorge.levendearbeidsforholdansettelse.repository;


import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobberRepository extends JpaRepository<JobbParameter, String> {
    //@Query("select * from JOBB_PARAM")
    List<JobbParameter> findAll();

    JobbParameter findByNavn(String navn);
}
