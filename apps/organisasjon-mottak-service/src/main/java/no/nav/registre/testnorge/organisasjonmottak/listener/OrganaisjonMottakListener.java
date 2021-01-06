package no.nav.registre.testnorge.organisasjonmottak.listener;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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
import no.nav.registre.testnorge.organisasjonmottak.domain.Flatfil;
import no.nav.registre.testnorge.organisasjonmottak.domain.Line;
import no.nav.registre.testnorge.organisasjonmottak.domain.Record;
import no.nav.registre.testnorge.organisasjonmottak.domain.ToLine;
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
    public void register(List<ConsumerRecord<String, SpecificRecord>> consumerRecords) {
        log.info("Starter innsending til organisasjon batch med antall records {}.", consumerRecords.size());
        consumerRecords
                .stream()
                .map(this::convert)
                .map(ToLine::toLine)
                .collect(Collectors.toList())
                .stream()
                .collect(Collectors.groupingBy(value -> value.getUuid() + value.getMiljo() + value.getEnhetstype() + value.getOrgnummer()))
                .values()
                .stream()
                .map(this::toRecord)
                .collect(Collectors.groupingBy(Record::getMiljo))
                .forEach((key, list) -> organisasjonService.save(
                        Flatfil.create(list),
                        key,
                        list.stream().map(Record::getUuid).collect(Collectors.toSet())
                ));
    }

    public Record toRecord(List<Line> lines) {
        var first = lines.get(0);
        return Record.create(
                lines,
                first.getOrgnummer(),
                first.getEnhetstype(),
                first.getUuid(),
                first.getMiljo()
        );
    }

    public ToLine convert(ConsumerRecord<String, SpecificRecord> consumerRecord) {
        var specificRecord = consumerRecord.value();
        var key = consumerRecord.key();

        if (specificRecord instanceof Organisasjon) {
            log.info("Oppretter ny organisasisjon med uuid: {}.", key);
            return new no.nav.registre.testnorge.organisasjonmottak.domain.Organisasjon(key, (Organisasjon) specificRecord);
        } else if (specificRecord instanceof Forretningsadresse) {
            log.info("Legger til forretningsadresse på organisasjon med uuid: {}.", key);
            return new no.nav.registre.testnorge.organisasjonmottak.domain.Forretningsadresse(key, (Forretningsadresse) specificRecord);
        } else if (specificRecord instanceof Navn) {
            log.info("Legger til navn på organisasjon med: {}.", key);
            return new no.nav.registre.testnorge.organisasjonmottak.domain.Navn(key, (Navn) specificRecord);
        } else if (specificRecord instanceof Ansatte) {
            log.info("Legger til ansatte på organisasjon med uuid: {}.", key);
            return new no.nav.registre.testnorge.organisasjonmottak.domain.Ansatte(key, (Ansatte) specificRecord);
        } else if (specificRecord instanceof DetaljertNavn) {
            log.info("Legger til detaljert navn på organisasjon med uuid: {}.", key);
            return new no.nav.registre.testnorge.organisasjonmottak.domain.DetaljertNavn(key, (DetaljertNavn) specificRecord);
        } else if (specificRecord instanceof Knytning) {
            log.info("Legger til knytning på organisasjon med uuid: {}.", key);
            return new no.nav.registre.testnorge.organisasjonmottak.domain.Knytning(key, (Knytning) specificRecord);
        } else if (specificRecord instanceof Postadresse) {
            log.info("Legger til postadresse på organisasjon med uuid: {}.", key);
            return new no.nav.registre.testnorge.organisasjonmottak.domain.Postadresse(key, (Postadresse) specificRecord);
        } else if (specificRecord instanceof Epost) {
            log.info("Legger til epost på organisasjon med uuid: {}.", key);
            return new no.nav.registre.testnorge.organisasjonmottak.domain.Epost(key, (Epost) specificRecord);
        } else if (specificRecord instanceof Internettadresse) {
            log.info("Legger til internettadresse på organisasjon med uuid: {}.", key);
            return new no.nav.registre.testnorge.organisasjonmottak.domain.Internettadresse(key, (Internettadresse) specificRecord);
        } else if (specificRecord instanceof Naeringskode) {
            log.info("Legger til næringskode på organisasjon med uuid: {}.", key);
            return new no.nav.registre.testnorge.organisasjonmottak.domain.Naeringskode(key, (Naeringskode) specificRecord);
        } else {
            throw new RuntimeException("Fikk ikke registrert verdi for type={}." + specificRecord.getClass());
        }
    }
}
