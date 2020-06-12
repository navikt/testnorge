package no.nav.registre.testnorge.helsepersonell.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.helsepersonell.consumer.HodejegerenConsumer;
import no.nav.registre.testnorge.helsepersonell.consumer.SamhandlerregisteretConsumer;
import no.nav.registre.testnorge.helsepersonell.domain.Lege;
import no.nav.registre.testnorge.helsepersonell.domain.LegeListe;

@Component
@RequiredArgsConstructor
public class HelsepersonellAdapter {
    private final SamhandlerregisteretConsumer samhandlerregisteretConsumer;
    private final HodejegerenConsumer hodejegerenConsumer;

    public LegeListe getLeger() {
        Set<String> leger = hodejegerenConsumer.getLeger();
        return new LegeListe(leger.stream()
                .map(ident -> new Lege(
                        samhandlerregisteretConsumer.getSamhandler(ident),
                        hodejegerenConsumer.getPersondata(ident))
                )
                .collect(Collectors.toList())
        );
    }
}
