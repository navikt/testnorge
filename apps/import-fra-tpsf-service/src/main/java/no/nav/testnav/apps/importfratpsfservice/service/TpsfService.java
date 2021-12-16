package no.nav.testnav.apps.importfratpsfservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.importfratpsfservice.consumer.PdlForvalterConsumer;
import no.nav.testnav.apps.importfratpsfservice.consumer.TpsfConsumer;
import no.nav.testnav.apps.importfratpsfservice.dto.SkdEndringsmelding;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class TpsfService {

    private final TpsfConsumer tpsfConsumer;
    private final PdlForvalterConsumer pdlForvalterConsumer;

    public Flux<SkdEndringsmelding> importIdenter(Long skdgruppeId, Long dollyGruppeId) {

        return tpsfConsumer.getSkdMeldinger(skdgruppeId, 0L);
    }
}
