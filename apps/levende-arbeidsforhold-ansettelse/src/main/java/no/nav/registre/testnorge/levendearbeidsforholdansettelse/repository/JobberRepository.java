package no.nav.registre.testnorge.levendearbeidsforholdansettelse.repository;


import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JobberRepository extends JpaRepository<JobbParameterEntity, String> {
    //@Query("select * from JOBB_PARAM")
    //List<JobbParameterEntity> findAll();
}
