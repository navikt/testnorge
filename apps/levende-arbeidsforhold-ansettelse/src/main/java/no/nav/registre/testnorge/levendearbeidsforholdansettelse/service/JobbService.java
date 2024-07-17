package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameterEntity;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.repository.JobberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobbService {


    private final JobberRepository jobberRepository;

    public List<JobbParameterEntity> hentAlle(){
        List<JobbParameterEntity> test = jobberRepository.findAll();
        log.info("Hentet fra h2 db: {}", test.toString());
        return test;
    }

    public void initDb(){
        JobbParameterEntity jobbParameterEntity = JobbParameterEntity.builder().param_tekst("Antall Organisasjoner").param_navn("antallOrganisasjoner").param_verdi("100.0").build();

        jobberRepository.save(jobbParameterEntity);
    }

/*
    @Autowired
    private JobberRepository jobberRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void skrivUtParametere(){
        log.info("Jobber:");
        hentParametere().forEach(param -> log.info(param.hentParamNavn()));
    }

    public List<JobbParameter> hentParametere(){
        return jobberRepository.findAll();
    }

 */
}
