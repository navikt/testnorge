package no.nav.registre.testnorge.organisasjonmottak.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import no.nav.registre.testnorge.organisasjonmottak.consumer.EregConsumer;
import no.nav.registre.testnorge.organisasjonmottak.domain.ToFlatfil;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganisasjonService {
    private final EregConsumer eregConsumer;

    public void save(ToFlatfil base, String miljo, String uuid) {
        log.info("Oppretter item med besrillings id: {}", uuid);
        eregConsumer.save(base.toFlatfil(), miljo, uuid);
    }
}
