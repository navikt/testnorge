package no.nav.registre.testnorge.organisasjonmottak.listener;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.libs.avro.organisasjon.Ansatte;
import no.nav.registre.testnorge.libs.avro.organisasjon.DetaljertNavn;
import no.nav.registre.testnorge.libs.avro.organisasjon.Epost;
import no.nav.registre.testnorge.libs.avro.organisasjon.Forretningsadresse;
import no.nav.registre.testnorge.libs.avro.organisasjon.Internettadresse;
import no.nav.registre.testnorge.libs.avro.organisasjon.Knytning;
import no.nav.registre.testnorge.libs.avro.organisasjon.Naeringskode;
import no.nav.registre.testnorge.libs.avro.organisasjon.Navn;
import no.nav.registre.testnorge.libs.avro.organisasjon.Organisasjon;
import no.nav.registre.testnorge.libs.avro.organisasjon.Postadresse;
import no.nav.registre.testnorge.libs.kafkaconfig.topic.OrganisasjonTopic;
import no.nav.registre.testnorge.organisasjonmottak.service.OrganisasjonService;

@Slf4j
@Profile("prod")
@Component
@RequiredArgsConstructor
public class OrganaisjonMottakListener {
    private final OrganisasjonService organisasjonService;

    @KafkaListener(topics = OrganisasjonTopic.ORGANISASJON_OPPRETT_ORGANISASJON)
    public void register(@Payload Organisasjon value, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String uuid) {
        log.info("Oppretter ny organiasjon med uuid: {}", uuid);
        organisasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottak.domain.Organisasjon(value),
                value.getMetadata().getMiljo(),
                uuid
        );
    }

    @KafkaListener(topics = OrganisasjonTopic.ORGANISASJON_SET_NAVN)
    public void register(@Payload Navn value, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String uuid) {
        log.info("Legger til navn på organasisjon med uuid: {}", uuid);
        organisasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottak.domain.Navn(value),
                value.getMetadata().getMiljo(),
                uuid
        );
    }

    @KafkaListener(topics = OrganisasjonTopic.ORGANISASJON_SET_ANSATTE)
    public void register(@Payload Ansatte value, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String uuid) {
        log.info("Legger til ansatte på organasisjon med uuid: {}", uuid);
        organisasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottak.domain.Ansatte(value),
                value.getMetadata().getMiljo(),
                uuid
        );
    }

    @KafkaListener(topics = OrganisasjonTopic.ORGANISASJON_SET_NAVN_DETALJER)
    public void register(@Payload DetaljertNavn value, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String uuid) {
        log.info("Legger til detaljert navn på organasisjon med uuid: {}", uuid);
        organisasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottak.domain.DetaljertNavn(value),
                value.getMetadata().getMiljo(),
                uuid
        );
    }

    @KafkaListener(topics = OrganisasjonTopic.ORGANISASJON_SET_KNYTNING)
    public void register(@Payload Knytning value, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String uuid) {
        log.info("Legger til knytning på organasisjon med uuid: {}", uuid);
        organisasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottak.domain.Knytning(value),
                value.getMetadata().getMiljo(),
                uuid
        );
    }

    @KafkaListener(topics = OrganisasjonTopic.ORGANISASJON_SET_FORRETNINGSADRESSE)
    public void register(@Payload Forretningsadresse value, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String uuid) {
        log.info("Legger til forretningsadresse på organasisjon med uuid: {}", uuid);
        organisasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottak.domain.Forretningsadresse(value),
                value.getMetadata().getMiljo(),
                uuid
        );
    }

    @KafkaListener(topics = OrganisasjonTopic.ORGANISASJON_SET_POSTADRESSE)
    public void register(@Payload Postadresse value, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String uuid) {
        log.info("Legger til postadresse på organasisjon med uuid: {}", uuid);
        organisasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottak.domain.Postadresse(value),
                value.getMetadata().getMiljo(),
                uuid
        );
    }

    @KafkaListener(topics = OrganisasjonTopic.ORGANISASJON_SET_EPOST)
    public void register(@Payload Epost value, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String uuid) {
        log.info("Legger til epost på organasisjon med uuid: {}", uuid);
        organisasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottak.domain.Epost(value),
                value.getMetadata().getMiljo(),
                uuid
        );
    }

    @KafkaListener(topics = OrganisasjonTopic.ORGANISASJON_SET_INTERNETTADRESSE)
    public void register(@Payload Internettadresse value, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String uuid) {
        log.info("Legger til internettadresse på organasisjon med uuid: {}", uuid);
        organisasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottak.domain.Internettadresse(value),
                value.getMetadata().getMiljo(),
                uuid
        );
    }

    @KafkaListener(topics = OrganisasjonTopic.ORGANISASJON_SET_NAERINGSKODE)
    public void register(@Payload Naeringskode value, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String uuid) {
        log.info("Legger til næringskode på organasisjon med uuid: {}", uuid);
        organisasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottak.domain.Naeringskode(value),
                value.getMetadata().getMiljo(),
                uuid
        );
    }
}
