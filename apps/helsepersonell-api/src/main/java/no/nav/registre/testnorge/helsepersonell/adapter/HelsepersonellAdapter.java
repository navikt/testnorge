package no.nav.registre.testnorge.helsepersonell.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.helsepersonell.consumer.HodejegerenConsumer;
import no.nav.registre.testnorge.helsepersonell.consumer.SamhandlerregisteretConsumer;
import no.nav.registre.testnorge.helsepersonell.domain.Lege;
import no.nav.registre.testnorge.helsepersonell.domain.LegeListe;
import no.nav.registre.testnorge.helsepersonell.domain.Samhandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class HelsepersonellAdapter {
    private final SamhandlerregisteretConsumer samhandlerregisteretConsumer;
    private final HodejegerenConsumer hodejegerenConsumer;


    private List<Samhandler> getSamhandlere(Set<String> idents) {
        return idents
                .stream()
                .map(samhandlerregisteretConsumer::getSamhandler).map(value -> {
                    try {
                        return value.get();
                    } catch (Exception e) {
                        log.error("Klarer ikke å hente samhandler", e);
                        throw new RuntimeException("Feil ved opprettelse av lege");
                    }
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public LegeListe getLeger() {
        Set<String> leger = hodejegerenConsumer.getLeger();
        List<Samhandler> samhandlere = getSamhandlere(leger)
                .stream()
                .filter(Samhandler::isMulighetForAaLageSykemelding)
                .collect(Collectors.toList());

        return new LegeListe(samhandlere.stream()
                .map(samhandler -> new Lege(
                        samhandler,
                        hodejegerenConsumer.getPersondata(samhandler.getIdent()))
                )
                .collect(Collectors.toList())
        );
    }
}
