package no.nav.registre.testnorge.organisasjonmottak.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import no.nav.registre.testnorge.organisasjonmottak.consumer.EregConsumer;
import no.nav.registre.testnorge.organisasjonmottak.domain.Flatfil;
import no.nav.registre.testnorge.organisasjonmottak.domain.Knyttning;
import no.nav.registre.testnorge.organisasjonmottak.domain.Organiasjon;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganiasjonService {
    private final FlatfilService flatfilService;
    private final EregConsumer eregConsumer;

    public void save(Knyttning knyttning, String miljo) {
        Flatfil flatfil = flatfilService.toFlatfil(knyttning);
        log.info("Lagerer knyttning i {}.", miljo);
        eregConsumer.save(flatfil, miljo);
    }

    public void save(Organiasjon organiasjon, String miljo) {
        Flatfil flatfil = flatfilService.toFlatfil(organiasjon);
        log.info("Lagerer organiasjon i {}.", miljo);
        eregConsumer.save(flatfil, miljo);
    }
}
