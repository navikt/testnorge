package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.entity.JobbParameterEntity;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class test {

    private final JobbService jobbService;
    private final KodeverkService kodeverkService;
    @EventListener(ApplicationReadyEvent.class)
    public void testDb() {
        jobbService.initDb();
        log.info("test");
        jobbService.hentAlle();

        jobbService.updateVerdi(JobbParameterEntity.builder().navn("antallOrganisasjoner").verdi("50").build());
        jobbService.hentAlle();
    }
    /*
    @EventListener(ApplicationReadyEvent.class)
    public void koverkTest(){
        Map<String, String> list = kodeverkService.hentKodeverk("yrker");
    }

     */
}
