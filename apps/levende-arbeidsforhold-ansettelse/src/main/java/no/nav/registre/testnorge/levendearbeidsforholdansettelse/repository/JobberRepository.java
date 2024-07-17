package no.nav.registre.testnorge.levendearbeidsforholdansettelse.repository;


import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobberRepository extends JpaRepository<JobbParameterEntity, Long> {

    public List<JobbParameterEntity> findAll();
}
