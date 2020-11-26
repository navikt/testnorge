package no.nav.registre.testnorge.organisasjonmottak.listener;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.libs.avro.organiasjon.Ansatte;
import no.nav.registre.testnorge.libs.avro.organiasjon.DetaljertNavn;
import no.nav.registre.testnorge.libs.avro.organiasjon.Navn;
import no.nav.registre.testnorge.libs.avro.organiasjon.Organiasjon;
import no.nav.registre.testnorge.organisasjonmottak.service.OrganiasjonService;

@Slf4j
@Profile("prod")
@Component
@RequiredArgsConstructor
public class OrganaisjonMottakListener {
    private final OrganiasjonService organiasjonService;

    @KafkaListener(topics = "tn-opprett-organiasjon-v1")
    public void opprettOrganiasjon(@Payload Organiasjon organiasjon) {
        organiasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottak.domain.Organiasjon(organiasjon),
                organiasjon.getMetadata().getMiljo()
        );
    }

    @KafkaListener(topics = "tn-organiasjon-set-navn-v1")
    public void opprettOrganiasjon(@Payload Navn navn) {
        organiasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottak.domain.Navn(navn),
                navn.getMetadata().getMiljo()
        );
    }

    @KafkaListener(topics = "tn-organiasjon-set-ansatte-v1")
    public void opprettOrganiasjon(@Payload Ansatte ansatte) {
        organiasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottak.domain.Ansatte(ansatte),
                ansatte.getMetadata().getMiljo()
        );
    }

    @KafkaListener(topics = "tn-organiasjon-set-navn-detaljer-v1")
    public void opprettOrganiasjon(@Payload DetaljertNavn detaljertNavn) {
        organiasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottak.domain.DetaljertNavn(detaljertNavn),
                detaljertNavn.getMetadata().getMiljo()
        );
    }

}
