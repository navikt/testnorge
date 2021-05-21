package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.HodejegerenConsumer;


@Slf4j
@Component
@RequiredArgsConstructor
public class TpsIdentAdapter implements IdentAdapter {
    private final HodejegerenConsumer hodejegerenConsumer;

    @Override
    public Flux<String> getIdenter(String miljo, int antall) {
        return hodejegerenConsumer.getIdenter(miljo, antall);
    }
}
