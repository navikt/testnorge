package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameter;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.repository.JobberRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobbService {

    private final JobberRepository jobberRepository;
    public List<JobbParameter> hentAlleParametere(){
        return jobberRepository.findAll();

    }

    public JobbParameter hentJobbParameter(String navn){
        return jobberRepository.findByNavn(navn);
    }

    public Map<String, String> hentParameterMap() {
        List<JobbParameter> jobbParametere = hentAlleParametere();
        Map<String, String> parameterMap = new HashMap<>();
        for (JobbParameter jobbParameterEntity : jobbParametere) {
            parameterMap.put(jobbParameterEntity.getNavn(), jobbParameterEntity.getVerdi());
        }
        return parameterMap;
    }

    public JobbParameter updateVerdi(JobbParameter jobbParameterEntity) {
        return jobberRepository.save(jobbParameterEntity);

    }
}
