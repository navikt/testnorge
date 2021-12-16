package no.nav.testnav.apps.importfratpsfservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.importfratpsfservice.consumer.PdlForvalterConsumer;
import no.nav.testnav.apps.importfratpsfservice.consumer.TpsfConsumer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TpsfService {

    private final TpsfConsumer tpsfConsumer;
    private final PdlForvalterConsumer pdlForvalterConsumer;

    public Flux<Object> importIdenter(Long skdgruppeId, Long dollyGruppeId) {

        return tpsfConsumer.getSkdMeldinger(skdgruppeId, 0L)
                .flatMapMany(chunk -> {
                    log.info("melding");
                    return Flux.just(List.of(chunk));
                });
    }
}
