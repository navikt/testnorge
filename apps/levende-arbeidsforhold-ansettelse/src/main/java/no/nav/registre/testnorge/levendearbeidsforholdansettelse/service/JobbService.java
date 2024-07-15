package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.Jobber;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.repository.JobberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class JobbService {

    @Autowired
    private JobberRepository jobberRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void skrivUtJobber(){
        log.info("Jobber:");
        Jobber jobb1 = hentJobber("123").getFirst();
        var ant = "" + jobb1.hentAntbedrifter();
        log.info(ant);
    }

    public List<Jobber> hentJobber(String id){
        var jobber = jobberRepository.findAllById(id);
        if(jobber.isPresent()){
            return jobber.orElse(new ArrayList<>());
        } else {
            return new ArrayList<>();
        }
    }
}
