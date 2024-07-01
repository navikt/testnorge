package no.nav.registre.testnorge.levendearbeidsforhold.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforhold.consumers.HentArbeidsforholdConsumer;
import no.nav.testnav.libs.dto.ameldingservice.v1.ArbeidsforholdDTO;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for å sjekke arbeidsforhold.
 * Henter arbeidsforhold fra Aareg og logger informasjonen.
 * @see no.nav.registre.testnorge.levendearbeidsforhold.consumers.HentArbeidsforholdConsumer
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdService {
    private final HentArbeidsforholdConsumer aaregArbeidsforholdConsumer;
    @EventListener(ApplicationReadyEvent.class)
    public void sjekkArbeidsforhold(String id) {
        List<ArbeidsforholdDTO> arbeidsforhold = aaregArbeidsforholdConsumer.getArbeidsforhold(id);
        if (arbeidsforhold != null) {
            log.info("Arbeidsforhold funnet: {}", arbeidsforhold);
        } else {
            log.warn("Fant ikke arbeidsforhold med id: {}", id);
        }
    }

}
//Les i appen ArbeidsforholdService