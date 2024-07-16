package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.AaregConsumer;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.v1.Arbeidsforhold;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdService {
    private final AaregConsumer aaregConsumer;
    @EventListener(ApplicationReadyEvent.class)
    public void getTestArbeidsforhold() {
        List<Arbeidsforhold> arbeidsforhold = aaregConsumer.hentArbeidsforhold("31456632311");
        log.info(arbeidsforhold.toString());
        log.info("arbeidsgiver: {}", arbeidsforhold.getFirst().getArbeidsgiver().getType());
    }
}
