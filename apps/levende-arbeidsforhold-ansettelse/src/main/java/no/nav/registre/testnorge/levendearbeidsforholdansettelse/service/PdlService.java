package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.PdlConsumer;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.provider.PdlMiljoer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdlService {
    private final PdlConsumer pdlConsumer;

    //@EventListener(ApplicationReadyEvent.class)
    public void getPerson(){
        String ident = "12345678901";
        PdlMiljoer miljoer = PdlMiljoer.Q2;
        //JsonNode pdl = pdlConsumer.getSokPerson(ident, miljoer).block();
        //log.info(pdl.toString());
    }
}
