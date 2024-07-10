package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.pdl.PdlPersonConsumer;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.provider.v2.PdlMiljoer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HentPersonService {
    private final PdlPersonConsumer pdlPersonConsumer;

    //@EventListener(ApplicationReadyEvent.class)
    public void hentPersoner() {
        System.out.println("Henter personer");
        String ident = "15476606168";
        PdlMiljoer miljoe = PdlMiljoer.Q2;
        log.info("pdlPersonConsumer.getPerson(\"12345678901\")");
        JsonNode pdl = pdlPersonConsumer.getPdlPerson(ident, miljoe).block();
        log.info("pdl: {}", pdl);

    }
}
