package no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.consumers.PdlConsumer;
import no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.provider.PdlMiljoer;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdlService {
    private final PdlConsumer pdlConsumer;

    public void getPerson(){
        String ident = "12345678901";
        PdlMiljoer miljoer = PdlMiljoer.Q2;
        pdlConsumer.getPdlPerson(ident, miljoer);
    }
}
