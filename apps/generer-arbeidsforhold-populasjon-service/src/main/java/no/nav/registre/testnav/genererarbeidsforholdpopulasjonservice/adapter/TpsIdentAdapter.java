package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.HodejegerenConsumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class TpsIdentAdapter implements IdentAdapter {
    private static final int MAX = 10000;
    private final HodejegerenConsumer hodejegerenConsumer;

    @Override
    public Set<String> getIdenter(String miljo, int antall) {
        if (antall <= MAX) {
            return hodejegerenConsumer.getIdenter(miljo, antall);
        }
        log.warn("Splitter opp henting av identer i hodejegern, men pageing er ikke implementert.");
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
