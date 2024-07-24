package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class test {

    private final JobbService jobbService;
    private final KodeverkService kodeverkService;
    //@EventListener(ApplicationReadyEvent.class)
    public void testDb() {
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
