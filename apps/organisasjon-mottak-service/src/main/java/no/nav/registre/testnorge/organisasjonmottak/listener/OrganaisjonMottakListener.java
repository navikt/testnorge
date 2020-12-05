package no.nav.registre.testnorge.organisasjonmottak.listener;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.libs.avro.organisasjon.Ansatte;
import no.nav.registre.testnorge.libs.avro.organisasjon.DetaljertNavn;
import no.nav.registre.testnorge.libs.avro.organisasjon.Knytning;
import no.nav.registre.testnorge.libs.avro.organisasjon.Navn;
import no.nav.registre.testnorge.libs.avro.organisasjon.Organisasjon;
import no.nav.registre.testnorge.organisasjonmottak.service.OrganisasjonService;

@Slf4j
@Profile("prod")
@Component
@RequiredArgsConstructor
public class OrganaisjonMottakListener {
    private final OrganisasjonService organisasjonService;

    @KafkaListener(topics = "tn-opprett-organisasjon-v1")
    public void register(@Payload Organisasjon value) {
        organisasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottak.domain.Organisasjon(value),
                value.getMetadata().getMiljo()
        );
    }

    @KafkaListener(topics = "tn-organisasjon-set-navn-v1")
    public void register(@Payload Navn value) {
        organisasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottak.domain.Navn(value),
                value.getMetadata().getMiljo()
        );
    }

    @KafkaListener(topics = "tn-organisasjon-set-ansatte-v1")
    public void register(@Payload Ansatte value) {
        organisasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottak.domain.Ansatte(value),
                value.getMetadata().getMiljo()
        );
    }

    @KafkaListener(topics = "tn-organisasjon-set-navn-detaljer-v1")
    public void register(@Payload DetaljertNavn value) {
        organisasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottak.domain.DetaljertNavn(value),
                value.getMetadata().getMiljo()
        );
    }

    @KafkaListener(topics = "tn-organisasjon-set-knytning-v2")
    public void register(@Payload Knytning value) {
        organisasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottak.domain.Knytning(value),
                value.getMetadata().getMiljo()
        );
    }

}
