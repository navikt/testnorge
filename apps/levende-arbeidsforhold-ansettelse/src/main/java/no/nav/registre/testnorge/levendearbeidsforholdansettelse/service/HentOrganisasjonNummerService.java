package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.OrganisasjonFasteDataConsumer;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class HentOrganisasjonNummerService {
    private final OrganisasjonFasteDataConsumer organisasjonFasteDataConsumer;

    @EventListener(ApplicationReadyEvent.class)
    public void hentOrganisasjoner() {
        List<String> orgNummer =  organisasjonFasteDataConsumer.hentOrganisasjoner();
        log.info("Hentet {} organisasjoner", orgNummer.size());
        log.info("Første org nr: {}", orgNummer.get(0));
    }
}
