package no.nav.registre.testnorge.organisasjonmottak.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

import no.nav.registre.testnorge.organisasjonmottak.consumer.EregConsumer;
import no.nav.registre.testnorge.organisasjonmottak.domain.Flatfil;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganisasjonService {
    private final EregConsumer eregConsumer;

    public void save(Flatfil flatfil, String miljo, Set<String> uuids) {
        eregConsumer.save(flatfil, miljo, uuids);
    }
}
