package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import no.nav.testnav.examples.reactiverestexample.JobbParameter;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class test {

    private final JobbService jobbService;
    private final KodeverkService kodeverkService;
    @EventListener(ApplicationReadyEvent.class)
    public void testDb() {

        List<JobbParameter> jobb2 = jobbService.hentAlleParametere();
        log.info("jobb2: {}", jobb2.toString() );

    }

}
