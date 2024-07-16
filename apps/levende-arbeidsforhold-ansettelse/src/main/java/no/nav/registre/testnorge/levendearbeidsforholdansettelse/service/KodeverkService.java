package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.KodeverkServiceConsumer;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.kodeverk.KodeverkNavn;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KodeverkService {
    private final KodeverkServiceConsumer kodeverkServiceConsumer;
    //@EventListener(ApplicationReadyEvent.class)
    public void hentKodever(){
        KodeverkNavn kode = KodeverkNavn.YRKER;
        log.info("Test kodeverknavn {}", KodeverkNavn.YRKER.value);
        //Map<String, String> kode = kodeverkServiceConsumer.hentKodeverk();

    }
}
