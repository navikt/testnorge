package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.HodejegerenConsumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class TpsIdentAdapter implements IdentAdapter {
    private static final int MAX = 10000;
    private final HodejegerenConsumer hodejegerenConsumer;

    @Override
    public Set<String> getIdenter(String miljo, int antall) {
        if (antall / 10000 >= 1) {
            return hodejegerenConsumer.getIdenter(miljo, antall);
        }
        log.warn("Splitter opp hening av identer i hodejegern, men pageing er ikke implemetert.");
        var identer = new HashSet<String>();
        for (int index = 0; index < (int) Math.floor((float) antall / MAX); index++) {
            identer.addAll(hodejegerenConsumer.getIdenter(miljo, MAX));
        }
        if (antall % MAX != 0) {
            identer.addAll(hodejegerenConsumer.getIdenter(miljo, antall % MAX));
        }
        return identer;
    }
}
