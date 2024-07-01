package no.nav.registre.testnorge.levendearbeidsforhold.service;

import no.nav.registre.testnorge.levendearbeidsforhold.config.Consumers;
import no.nav.registre.testnorge.levendearbeidsforhold.consumers.HentArbeidsforholdConsumer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class Test {
    private final String id = "30447515845";

    @EventListener(ApplicationReadyEvent.class)
    public void sjekkArbeidsforhold() {
        ArbeidsforholdService arbeidsforholdService = new ArbeidsforholdService(new HentArbeidsforholdConsumer(
                new Consumers().getAaregConsumer()
        ));
    }
}
