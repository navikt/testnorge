package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import no.nav.testnav.examples.reactiverestexample.JobbParameter;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameter;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameterVerdier;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.Verdier;
import org.quartz.Job;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class test {

    private final JobbService jobbService;
    private final KodeverkService kodeverkService;
    //@EventListener(ApplicationReadyEvent.class)
    public void testDb() {

        List<JobbParameter> jobb2 = jobbService.hentAlleParametere();
        log.info("jobb2: {}", jobb2.toString() );
        //List<JobbParameterVerdier> jobb = jobbService.hentAlleParametereMedVerdier();
        //log.info("JobbParameteVerdier: {}", jobb.toString());




        /*
        List<JobbParameter> jobb = jobbService.hentAlleParametere();
        List<String> test = jobbService.finnAlleVerdier(jobb.getFirst());
        log.info("test å hente verdier: {}", test.toString());

         */
        /*
        jobbService.initDb();
        log.info("test");
        jobbService.hentAlleParametere();

        //jobbService.updateVerdi(JobbParameterEntity.builder().navn("antallOrganisasjoner").verdi("50").build());

        jobbService.hentAlleParametere();

        jobbService.hentAlleMedNavn("antallOrganisasjoner");

         */
    }
    /*
    @EventListener(ApplicationReadyEvent.class)
    public void koverkTest(){
        Map<String, String> list = kodeverkService.hentKodeverk("yrker");
    }

     */
}
