package no.nav.testnav.levendearbeidsforholdansettelse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.levendearbeidsforholdansettelse.consumers.KodeverkServiceConsumer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Klasse for å hente koder fra kodeverk-service
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KodeverkService {
    private final KodeverkServiceConsumer kodeverkServiceConsumer;

    /**
     * Henter koder for ett gitt kodeverk.
     * @param kodeverk String som er navnet på kodeverket.
     * @return En liste av String som er kodene.
     */
    public Mono<List<String>> hentKodeverkValues(String kodeverk){

        return kodeverkServiceConsumer.hentKodeverk(kodeverk);
    }
}
