package no.nav.registre.testnorge.organisasjonmottakservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import no.nav.registre.testnorge.organisasjonmottakservice.domain.Flatfil;
import no.nav.registre.testnorge.organisasjonmottakservice.domain.Knyttning;
import no.nav.registre.testnorge.organisasjonmottakservice.domain.Organiasjon;

@Slf4j
@Service
public class OrganiasjonService {

    public OrganiasjonService(FlatfilService flatfilService) {
        this.flatfilService = flatfilService;

        log.info("############################################");
        log.info("{}", String.join(",", System.getProperties().keySet().stream().map(Object::toString).collect(Collectors.toSet())));
        log.info("############################################");
    }

    private final FlatfilService flatfilService;
    //private final EregConsumer eregConsumer;

    public void save(Knyttning knyttning, String miljo) {
        Flatfil flatfil = flatfilService.toFlatfil(knyttning);
        log.info("Lagerer knyttning i {}.", miljo);
        //eregConsumer.save(flatfil, miljo);
    }

    public void save(Organiasjon organiasjon, String miljo) {
        Flatfil flatfil = flatfilService.toFlatfil(organiasjon);
        log.info("Lagerer organiasjon i {}.", miljo);
        //eregConsumer.save(flatfil, miljo);
    }
}
