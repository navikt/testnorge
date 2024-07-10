package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.TenorConsumer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TenorPersonService {

    private final TenorConsumer tenorConsumer;

    //@EventListener(ApplicationReadyEvent.class)
    public void tenorPersonService() throws JsonProcessingException {
        log.info("Henter tenor person");
        tenorConsumer.consume();
    }
}
