package no.nav.registre.testnorge.organisasjonmottak.listener;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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

    @KafkaListener(topics = {
            OrganisasjonTopic.ORGANISASJON_OPPRETT_ORGANISASJON,
            OrganisasjonTopic.ORGANISASJON_SET_FORRETNINGSADRESSE,
            OrganisasjonTopic.ORGANISASJON_SET_NAVN,
            OrganisasjonTopic.ORGANISASJON_SET_NAVN_DETALJER,
            OrganisasjonTopic.ORGANISASJON_SET_KNYTNING,
            OrganisasjonTopic.ORGANISASJON_SET_ANSATTE,
            OrganisasjonTopic.ORGANISASJON_SET_POSTADRESSE,
            OrganisasjonTopic.ORGANISASJON_SET_EPOST,
            OrganisasjonTopic.ORGANISASJON_SET_INTERNETTADRESSE,
            OrganisasjonTopic.ORGANISASJON_SET_NAERINGSKODE,
    })
    public void register(ConsumerRecord<String, SpecificRecord> record) {
        if (record.value() instanceof Organisasjon) {
            register((Organisasjon) record.value(), record.key(), record.partition());
        } else if (record.value() instanceof Forretningsadresse) {
            register((Forretningsadresse) record.value(), record.key(), record.partition());
        } else if (record.value() instanceof Navn) {
            register((Navn) record.value(), record.key(), record.partition());
        } else if (record.value() instanceof Ansatte) {
            register((Ansatte) record.value(), record.key(), record.partition());
        } else if (record.value() instanceof DetaljertNavn) {
            register((DetaljertNavn) record.value(), record.key(), record.partition());
        } else if (record.value() instanceof Knytning) {
            register((Knytning) record.value(), record.key(), record.partition());
        } else if (record.value() instanceof Postadresse) {
            register((Postadresse) record.value(), record.key(), record.partition());
        } else if (record.value() instanceof Epost) {
            register((Epost) record.value(), record.key(), record.partition());
        } else if (record.value() instanceof Internettadresse) {
            register((Internettadresse) record.value(), record.key(), record.partition());
        } else if (record.value() instanceof Naeringskode) {
            register((Naeringskode) record.value(), record.key(), record.partition());
        } else {
            log.error("Fant ikke for type={}.", record.value().getClass());
        }
    }

    private void register(Organisasjon value, String uuid, int partition) {
        log.info("Oppretter ny organisasjon med uuid: {} og partition: {}", uuid, partition);
        organisasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottak.domain.Organisasjon(value),
                value.getMetadata().getMiljo(),
                uuid
        );
    }


    private void register(Navn value, String uuid, int partition) {
        log.info("Legger til navn på organasisjon med uuid: {} og partition: {}", uuid, partition);
        organisasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottak.domain.Navn(value),
                value.getMetadata().getMiljo(),
                uuid
        );
    }

    private void register(Ansatte value, String uuid, int partition) {
        log.info("Legger til ansatte på organasisjon med uuid: {} og partition: {}", uuid, partition);
        organisasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottak.domain.Ansatte(value),
                value.getMetadata().getMiljo(),
                uuid
        );
    }

    private void register(DetaljertNavn value, String uuid, int partition) {
        log.info("Legger til detaljert navn på organasisjon med uuid: {} og partition: {}", uuid, partition);
        organisasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottak.domain.DetaljertNavn(value),
                value.getMetadata().getMiljo(),
                uuid
        );
    }

    private void register(Knytning value, String uuid, int partition) {
        log.info("Legger til knytning på organasisjon med uuid: {} og partition: {}", uuid, partition);
        organisasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottak.domain.Knytning(value),
                value.getMetadata().getMiljo(),
                uuid
        );
    }


    private void register(Forretningsadresse value, String uuid, int partition) {
        log.info("Legger til forretningsadresse på organasisjon med uuid: {} og partition: {}", uuid, partition);
        organisasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottak.domain.Forretningsadresse(value),
                value.getMetadata().getMiljo(),
                uuid
        );
    }


    private void register(Postadresse value, String uuid, int partition) {
        log.info("Legger til postadresse på organasisjon med uuid: {} og partition: {}", uuid, partition);
        organisasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottak.domain.Postadresse(value),
                value.getMetadata().getMiljo(),
                uuid
        );
    }

    private void register(Epost value, String uuid, int partition) {
        log.info("Legger til epost på organasisjon med uuid: {} og partition: {}", uuid, partition);
        organisasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottak.domain.Epost(value),
                value.getMetadata().getMiljo(),
                uuid
        );
    }


    private void register(Internettadresse value,String uuid, int partition) {
        log.info("Legger til internettadresse på organasisjon med uuid: {} og partition: {}", uuid, partition);
        organisasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottak.domain.Internettadresse(value),
                value.getMetadata().getMiljo(),
                uuid
        );
    }


    private void register(Naeringskode value, String uuid, int partition) {
        log.info("Legger til næringskode på organasisjon med uuid: {} og partition: {}", uuid, partition);
        organisasjonService.save(
                new no.nav.registre.testnorge.organisasjonmottak.domain.Naeringskode(value),
                value.getMetadata().getMiljo(),
                uuid
        );
    }
}
