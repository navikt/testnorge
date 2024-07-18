package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameterEntity;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.repository.JobberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        JobbParameterEntity jobbParameterEntity = JobbParameterEntity.builder().tekst("Antall Organisasjoner").navn("antallOrganisasjoner").verdi("100.0").build();

        jobberRepository.save(jobbParameterEntity);

        JobbParameterEntity jobb2 = JobbParameterEntity.builder().navn("antallPersoner").tekst("Antall personer").verdi("20").build();
        jobberRepository.save(jobb2);
        JobbParameterEntity jobb3 = JobbParameterEntity.builder().navn("typeArbeidsforhold").tekst("Type Arbeidsforhold").verdi("ordinaertArbeidsforhold").build();
        jobberRepository.save(jobb3);
        JobbParameterEntity jobb4 = JobbParameterEntity.builder().navn("arbeidstidsOrdning").tekst("Arbeidstids Ordning").verdi("ikkeSkift").build();
        jobberRepository.save(jobb4);
        JobbParameterEntity jobb5 = JobbParameterEntity.builder().navn("stillingsprosent").tekst("Stillingsprosent").verdi("100.0").build();
        jobberRepository.save(jobb5);
    }

    public JobbParameterEntity updateVerdi(JobbParameterEntity jobbParameterEntity) {
        JobbParameterEntity jobb = jobberRepository.findByNavn(jobbParameterEntity.getNavn());

        jobb.setVerdi(jobbParameterEntity.getVerdi());

        jobberRepository.save(jobb);
        return jobb;

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
