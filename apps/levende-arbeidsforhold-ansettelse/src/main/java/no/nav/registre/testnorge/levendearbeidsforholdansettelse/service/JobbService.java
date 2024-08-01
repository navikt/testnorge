package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameter;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.repository.JobberRepository;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Service klassen til jobberRepository
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JobbService {

    private final JobberRepository jobberRepository;

    /**
     * Henter alle parameterne i jobb_porameter db.
     * @return Returnerer en liste av JobbParameterObjektene til alle parameterne
     */
    public List<JobbParameter> hentAlleParametere(){
        return jobberRepository.findAll();

    }

    /**
     * Hente en spesifikk parameter i db
     * @param navn Navnet til parameteren man skal hente
     * @return JobbParameter objektet til parameteren.
     */
    public JobbParameter hentJobbParameter(String navn){
        return jobberRepository.findByNavn(navn);
    }

    /**
     * Funksjon for å lage ett map av navnet og verdien til parameterne for å gjøre det lettere å bruke.
     * @return ett map av JobbParameter navnet og verdier
     */
    public Map<String, String> hentParameterMap() {
        List<JobbParameter> jobbParametere = hentAlleParametere();
        Map<String, String> parameterMap = new HashMap<>();
        for (JobbParameter jobbParameterEntity : jobbParametere) {
            parameterMap.put(jobbParameterEntity.getNavn(), jobbParameterEntity.getVerdi());
        }
        return parameterMap;
    }

    /**
     * Funksjon for å oppdatere en verdi i db.
     * @param jobbParameterEntity Objektet som skal oppdateres i
     * @return
     */
    public JobbParameter updateVerdi(JobbParameter jobbParameterEntity) {
        return jobberRepository.save(jobbParameterEntity);

    }
}
