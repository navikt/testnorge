package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.KodeverkServiceConsumer;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.kodeverk.KodeverkNavn;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KodeverkService {
    private final KodeverkServiceConsumer kodeverkServiceConsumer;
    //@EventListener(ApplicationReadyEvent.class)
    public Map<String, String> hentKodever(String kodeverk){
        return kodeverkServiceConsumer.hentKodeverk(kodeverk);
    }

    public List<String> hentKodeverkValues(String kodeverk){
        return kodeverkServiceConsumer.hentKodeverkListe(kodeverk);
    }
}
