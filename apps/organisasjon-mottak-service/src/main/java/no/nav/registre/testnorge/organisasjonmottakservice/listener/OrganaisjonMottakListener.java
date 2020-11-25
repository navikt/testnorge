package no.nav.registre.testnorge.organisasjonmottakservice.listener;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.libs.avro.organiasjon.Knyttning;
import no.nav.registre.testnorge.libs.avro.organiasjon.Organiasjon;
import no.nav.registre.testnorge.organisasjonmottakservice.service.OrganiasjonService;

@Slf4j
@Profile("prod")
@RequiredArgsConstructor
public class OrganaisjonMottakListener {
    private final OrganiasjonService organiasjonService;

    @KafkaListener(topics = "tn-opprett-organiasjon-v1")
    public void opprettOrganiasjon(@Payload Organiasjon organiasjon) {
        organiasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottakservice.domain.Organiasjon(organiasjon),
                organiasjon.getMiljo()
        );
    }

    @KafkaListener(topics = "tn-opprett-knyttninger-mellom-organiasjoner-v1")
    public void opprettKnyttning(@Payload Knyttning knyttning) {
        organiasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottakservice.domain.Knyttning(knyttning),
                knyttning.getMiljo()
        );
    }
}
