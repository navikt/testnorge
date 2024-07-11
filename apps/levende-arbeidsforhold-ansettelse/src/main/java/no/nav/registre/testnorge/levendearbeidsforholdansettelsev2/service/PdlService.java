package no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.consumers.PdlConsumer;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdlService {
    private final PdlConsumer pdlConsumer;

    public void getPerson(){
        pdlConsumer.getPdlPerson();
    }
}
