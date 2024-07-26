package no.nav.registre.testnorge.levendearbeidsforholdansettelse.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.KodeverkServiceConsumer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KodeverkService {
    private final KodeverkServiceConsumer kodeverkServiceConsumer;

    public List<String> hentKodeverkValues(String kodeverk){
        return kodeverkServiceConsumer.hentKodeverkListe(kodeverk);
    }

}
