package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameterEntity;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.VerdierEntity;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.repository.JobberRepository;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.repository.VerdiRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobbService {


    private final JobberRepository jobberRepository;
    private final VerdiRepository verdiRepository;
    public List<JobbParameterEntity> hentAlleParametere(){
        List<JobbParameterEntity> test = jobberRepository.findAll();
        log.info("Hentet fra h2: {}", test.toString());
        return test;
    }

    public List<VerdierEntity> hentAlleMedNavn(String navn){
        //JobbParameterEntity org = jobberRepository.findByNavn("antallOrganisasjoner");
        //org.getVerdier();
        //List<VerdierEntity> test = verdiRepository.findByNavn(navn);
        //log.info("Henter fra verdier table {}", test.toString());
        return null;
    }

    public Map<String, String> hentParameterMap() {
        List<JobbParameterEntity> jobbParametere = hentAlleParametere();
        Map<String, String> parameterMap = new HashMap<>();
        for (JobbParameterEntity jobbParameterEntity : jobbParametere) {
            parameterMap.put(jobbParameterEntity.getNavn(), jobbParameterEntity.getVerdi());
        }
        return parameterMap;
    }

    public void initDb(){
        JobbParameterEntity jobbParameterEntity = JobbParameterEntity.builder().tekst("Antall Organisasjoner").navn("antallOrganisasjoner").verdi("100.0").build();
        jobberRepository.save(jobbParameterEntity);
        JobbParameterEntity jobb2 = JobbParameterEntity.builder().navn("antallPersoner").tekst("Antall personer").verdi("20").build();
        jobberRepository.save(jobb2);
    }

    public void lagreParameter(JobbParameterEntity jobbParameterEntity){
        jobberRepository.save(jobbParameterEntity);
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
