package no.nav.registre.testnorge.helsepersonellservice.adapter;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.testnorge.helsepersonellservice.consumer.HodejegerenConsumer;
import no.nav.registre.testnorge.helsepersonellservice.consumer.SamhandlerregisteretConsumer;
import no.nav.registre.testnorge.helsepersonellservice.domain.Helsepersonell;
import no.nav.registre.testnorge.helsepersonellservice.domain.HelsepersonellListe;
import no.nav.registre.testnorge.helsepersonellservice.domain.Samhandler;
import no.nav.registre.testnorge.helsepersonellservice.exception.UgyldigSamhandlerException;

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
                        throw new UgyldigSamhandlerException("Feil ved opprettelse av helsepersonell");
                    }
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    public HelsepersonellListe getHelsepersonell() {
        Set<String> helsepersonell = hodejegerenConsumer.getHelsepersonell();
        List<Samhandler> samhandlere = getSamhandlere(helsepersonell)
                .stream()
                .filter(Samhandler::isMulighetForAaLageSykemelding)
                .collect(Collectors.toList());

        return new HelsepersonellListe(samhandlere.stream()
                .map(samhandler -> new Helsepersonell(
                        samhandler,
                        hodejegerenConsumer.getPersondata(samhandler.getIdent())))
                .distinct()
                .collect(Collectors.toList())
        );
    }
}
