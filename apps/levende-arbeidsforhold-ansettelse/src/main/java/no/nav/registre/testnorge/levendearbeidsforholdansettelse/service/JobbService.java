package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameterEntity;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.repository.JobberRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobbService {


    private final JobberRepository jobberRepository;

    public List<JobbParameterEntity> hentAlleParametere(){
        List<JobbParameterEntity> jobbParametere = jobberRepository.findAll();
        log.info("Hentet fra h2 db: {}", jobbParametere.toString());
        return jobbParametere;
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
        JobbParameterEntity antallOrganisasjoner = JobbParameterEntity.builder().tekst("Antall Organisasjoner").navn("antallOrganisasjoner").verdi("100.0").build();

        jobberRepository.save(antallOrganisasjoner);

        JobbParameterEntity antallPersoner = JobbParameterEntity.builder().navn("antallPersoner").tekst("Antall personer").verdi("20").build();
        jobberRepository.save(antallPersoner);
        JobbParameterEntity arbeidsforholdType = JobbParameterEntity.builder().navn("typeArbeidsforhold").tekst("Type Arbeidsforhold").verdi("ordinaertArbeidsforhold").build();
        jobberRepository.save(arbeidsforholdType);
        JobbParameterEntity arbeidstidsordning = JobbParameterEntity.builder().navn("arbeidstidsordning").tekst("Arbeidstids Ordning").verdi("ikkeSkift").build();
        jobberRepository.save(arbeidstidsordning);
        JobbParameterEntity stillingsprosent = JobbParameterEntity.builder().navn("stillingsprosent").tekst("Stillingsprosent").verdi("100.0").build();
        jobberRepository.save(stillingsprosent);
    }

    public JobbParameterEntity updateVerdi(JobbParameterEntity jobbParameterEntity) {
        JobbParameterEntity parameter = jobberRepository.findByNavn(jobbParameterEntity.getNavn());
        parameter.setVerdi(jobbParameterEntity.getVerdi());
        jobberRepository.save(parameter);
        return parameter;


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
