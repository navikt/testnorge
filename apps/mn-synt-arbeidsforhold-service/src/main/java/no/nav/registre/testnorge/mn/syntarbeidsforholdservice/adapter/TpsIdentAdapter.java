package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.HodejegerenConsumer;

@Component
@RequiredArgsConstructor
public class TpsIdentAdapter implements IdentAdapter {
    private final HodejegerenConsumer hodejegerenConsumer;

    @Override
    public Set<String> getIdenter(String miljo, int antall) {
        return hodejegerenConsumer.getIdenter(miljo, antall);
    }
}
