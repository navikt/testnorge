package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.Jobber;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.repository.JobberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JobbService {

    @Autowired
    private JobberRepository jobberRepository;

    public List<Jobber> hentJobber(String id){
        var jobber = jobberRepository.findAllById(id);
        if(jobber.isPresent()){
            return jobber.orElse(new ArrayList<>());
        } else {
            return new ArrayList<>();
        }
    }
}
