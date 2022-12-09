package no.nav.registre.testnorge.helsepersonellservice.service;

import java.util.*;

import no.nav.registre.testnorge.helsepersonellservice.consumer.DollyBackendConsumer;
import no.nav.registre.testnorge.helsepersonellservice.consumer.PdlProxyConsumer;
import no.nav.registre.testnorge.helsepersonellservice.domain.HelsepersonellListe;
import no.nav.registre.testnorge.helsepersonellservice.domain.Helsepersonell;
import no.nav.registre.testnorge.helsepersonellservice.domain.PdlPersonBolk;
import no.nav.registre.testnorge.helsepersonellservice.domain.Persondata;
import no.nav.registre.testnorge.helsepersonellservice.domain.Samhandler;
import no.nav.registre.testnorge.helsepersonellservice.exception.SamhandlereNotFoundException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.testnorge.helsepersonellservice.consumer.SamhandlerregisteretConsumer;
import no.nav.registre.testnorge.helsepersonellservice.exception.UgyldigSamhandlerException;
import reactor.core.publisher.Flux;

import static java.util.Objects.isNull;

@Slf4j
@Component
@RequiredArgsConstructor
public class HelsepersonellService {
    private final SamhandlerregisteretConsumer samhandlerregisteretConsumer;
    private final DollyBackendConsumer dollyBackendConsumer;
    private final PdlProxyConsumer pdlProxyConsumer;

    private List<Samhandler> getSamhandlere(List<String> identer) {
        return samhandlerregisteretConsumer.getToken()
                .flatMapMany(accessToken -> Flux.fromIterable(identer)
                        .flatMap(ident -> samhandlerregisteretConsumer.getSamhandler(ident, accessToken))
                        .filter(Objects::nonNull)
                ).collectList()
                .block();
    }

    public HelsepersonellListe getHelsepersonell() {
        var helsepersonell = dollyBackendConsumer.getHelsepersonell();
        var pdlInfo = pdlProxyConsumer.getPdlPersoner(helsepersonell);

        var samhandlere = Optional.ofNullable(getSamhandlere(helsepersonell))
                .orElse(Collections.emptyList())
                .stream()
                .filter(Samhandler::isMulighetForAaLageSykemelding)
                .toList();
        if (samhandlere.isEmpty()) {
            throw new SamhandlereNotFoundException("Fant ingen samhandlere");
        }
        return new HelsepersonellListe(samhandlere.stream()
                .map(samhandler -> new Helsepersonell(
                        samhandler,
                        getPersondata(pdlInfo, samhandler.getIdent())))
                .distinct().toList()
        );
    }

    private Persondata getPersondata(PdlPersonBolk pdlBolk, String ident) {
        var pdlPerson = pdlBolk.getData().getHentPersonBolk().stream()
                .filter(personBolk -> personBolk.getIdent().equals(ident))
                .findFirst()
                .orElse(null);

        return new Persondata(pdlPerson);
    }
}
