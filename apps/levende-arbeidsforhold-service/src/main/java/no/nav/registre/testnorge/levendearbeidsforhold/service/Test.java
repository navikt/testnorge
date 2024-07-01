package no.nav.registre.testnorge.levendearbeidsforhold.service;

import no.nav.registre.testnorge.levendearbeidsforhold.config.Consumers;
import no.nav.registre.testnorge.levendearbeidsforhold.consumers.HentArbeidsforholdConsumer;
import no.nav.registre.testnorge.levendearbeidsforhold.consumers.command.HentArbeidsforholdCommand;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class Test {
    private final String id = "30447515845";

    @EventListener(ApplicationReadyEvent.class)
    public void sjekkArbeidsforhold() {
        WebClient webClient = WebClient.create();


    }
}
