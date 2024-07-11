package no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.consumers.EregConsumer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HentOrganisasjonerService {
    private final EregConsumer eregConsumer;
    @EventListener(ApplicationReadyEvent.class)
    public void hentOrganisasjoner() {
        System.out.println("Henter organisasjoner");
        log.info("eregConsumer.getOrganisasjon(\"955937864\", \"q2\")");
        eregConsumer.getOrganisasjon("955937864", "q2");
    }
}
