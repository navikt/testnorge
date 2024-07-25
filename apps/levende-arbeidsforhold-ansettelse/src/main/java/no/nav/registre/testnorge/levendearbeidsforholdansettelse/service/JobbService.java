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


    public List<String> finnAlleVerdier(JobbParameter jobbParameter){
        List<String> verdierListe = new ArrayList<>();
        verdiRepository.findVerdierByVerdiNavn(jobbParameter).forEach(verdier -> verdierListe.add(verdier.getVerdiVerdi()));
        return  verdierListe;

    }
    public Map<String, String> hentParameterMap() {
        List<JobbParameter> jobbParametere = hentAlleParametere();
        Map<String, String> parameterMap = new HashMap<>();
        for (JobbParameter jobbParameterEntity : jobbParametere) {
            parameterMap.put(jobbParameterEntity.getNavn(), jobbParameterEntity.getVerdi());
        }
        return parameterMap;
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
}
