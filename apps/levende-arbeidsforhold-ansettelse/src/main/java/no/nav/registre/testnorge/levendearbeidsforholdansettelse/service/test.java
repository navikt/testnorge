package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class test {

    private final JobbService jobbService;

    @EventListener(ApplicationReadyEvent.class)
    public void testDb() {
        jobbService.initDb();
        log.info("test");
        jobbService.hentAlle();
    }
}
