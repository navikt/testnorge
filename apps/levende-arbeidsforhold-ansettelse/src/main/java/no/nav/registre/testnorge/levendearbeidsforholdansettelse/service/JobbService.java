package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameter;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.Verdier;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.repository.JobberRepository;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.repository.VerdiRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobbService {


    private final JobberRepository jobberRepository;
    private final VerdiRepository verdiRepository;
    public List<JobbParameter> hentAlleParametere(){
        List<JobbParameter> test = jobberRepository.findAll();
        return test;
    }

    public JobbParameter hentJobbParameter(String navn){
        return jobberRepository.findByNavn(navn);
    }
    public List<Verdier> hentAlleMedNavn(JobbParameter jobbParameter){
        //JobbParameter org = jobberRepository.findByNavn(navn);
        //org.getVerdier();
        List<Verdier> test = verdiRepository.findByVerdiNavn(jobbParameter);
        //List<VerdierEntity> test2 = verdiRepository.hentVerdier(navn);

        log.info("Henter fra verdier table {}", test.toString());
        //log.info("Henter navn fra table {}", test2.toString());
        return test;
    }

    public Map<String, String> hentParameterMap() {
        List<JobbParameter> jobbParametere = hentAlleParametere();
        Map<String, String> parameterMap = new HashMap<>();
        for (JobbParameter jobbParameterEntity : jobbParametere) {
            parameterMap.put(jobbParameterEntity.getNavn(), jobbParameterEntity.getVerdi());
        }
        return parameterMap;
    }

    public void initDb(){
        /*
        JobbParameterEntity jobbParameterEntity = JobbParameterEntity.builder().tekst("Antall Organisasjoner").navn("antallOrganisasjoner").verdi("100.0").build();
        jobberRepository.save(jobbParameterEntity);
        //JobbParameterEntity jobb2 = JobbParameterEntity.builder().navn("antallPersoner").tekst("Antall personer").verdi("20").build();
        //jobberRepository.save(jobb2);
        List<VerdierEntity> verdierEntities = new ArrayList<>();
        for(int i = 20; i<=100; i+=20){
            VerdierEntity verdier = VerdierEntity.builder().navn(jobbParameterEntity.getNavn()).verdi(String.valueOf(i)).build();
            verdierEntities.add(verdier);
            verdiRepository.save(verdier);
        }
        jobbParameterEntity.setVerdier(verdierEntities);
        jobberRepository.save(jobbParameterEntity);

         */

    }

    public void lagreParameter(JobbParameter jobbParameterEntity){
        jobberRepository.save(jobbParameterEntity);
    }

    public JobbParameter updateVerdi(JobbParameter jobbParameterEntity) {
        JobbParameter jobb = jobberRepository.findByNavn(jobbParameterEntity.getNavn());

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
