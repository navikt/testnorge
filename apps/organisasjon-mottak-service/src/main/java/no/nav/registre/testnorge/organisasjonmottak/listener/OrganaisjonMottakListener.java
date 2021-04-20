package no.nav.registre.testnorge.organisasjonmottak.listener;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Endringsdokument;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Opprettelsesdokument;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Organisasjon;
import no.nav.registre.testnorge.libs.kafkaconfig.topic.v2.OrganisasjonTopic;
import no.nav.registre.testnorge.organisasjonmottak.consumer.JenkinsConsumer;
import no.nav.registre.testnorge.organisasjonmottak.domain.Ansatte;
import no.nav.registre.testnorge.organisasjonmottak.domain.DetaljertNavn;
import no.nav.registre.testnorge.organisasjonmottak.domain.Epost;
import no.nav.registre.testnorge.organisasjonmottak.domain.Flatfil;
import no.nav.registre.testnorge.organisasjonmottak.domain.Formaal;
import no.nav.registre.testnorge.organisasjonmottak.domain.Forretningsadresse;
import no.nav.registre.testnorge.organisasjonmottak.domain.Internettadresse;
import no.nav.registre.testnorge.organisasjonmottak.domain.Knytning;
import no.nav.registre.testnorge.organisasjonmottak.domain.Maalform;
import no.nav.registre.testnorge.organisasjonmottak.domain.Naeringskode;
import no.nav.registre.testnorge.organisasjonmottak.domain.Postadresse;
import no.nav.registre.testnorge.organisasjonmottak.domain.Record;
import no.nav.registre.testnorge.organisasjonmottak.domain.Sektorkode;
import no.nav.registre.testnorge.organisasjonmottak.domain.Stiftelsesdato;
import no.nav.registre.testnorge.organisasjonmottak.domain.Telefon;
import no.nav.registre.testnorge.organisasjonmottak.domain.ToLine;

@Slf4j
@Profile("prod")
@Component
@RequiredArgsConstructor
public class OrganaisjonMottakListener {
    private final JenkinsConsumer jenkinsConsumer;


    @KafkaListener(topics = OrganisasjonTopic.ORGANISASJON_OPPRETT_ORGANISASJON)
    public void create(List<ConsumerRecord<String, Opprettelsesdokument>> records) {
        records.stream().collect(Collectors.groupingBy(value -> value.value().getMetadata().getMiljo())).forEach((miljo, list) -> {
            var map = list.stream().collect(Collectors.toMap(ConsumerRecord::key, value -> value.value().getOrganisasjon()));
            save(map, miljo, false);
        });
    }

    @KafkaListener(topics = OrganisasjonTopic.ORGANISASJON_ENDRE_ORGANISASJON)
    public void update(List<ConsumerRecord<String, Endringsdokument>> records) {
        records.stream().collect(Collectors.groupingBy(value -> value.value().getMetadata().getMiljo())).forEach((miljo, list) -> {
            var map = list.stream().collect(Collectors.toMap(ConsumerRecord::key, value -> value.value().getOrganisasjon()));
            save(map, miljo, true);
        });
    }

    private void save(Map<String, Organisasjon> organisasjonMap, String miljo, boolean update) {
        var lines = organisasjonMap
                .values()
                .stream()
                .map(organisajon -> createRecords(organisajon, update))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        jenkinsConsumer.send(Flatfil.create(lines), miljo, organisasjonMap.keySet());
    }

    private List<Record> createRecords(Organisasjon organisasjon, boolean update) {
        List<Record> list = new ArrayList<>();
        createRecords(list, organisasjon, null, update);
        return list;
    }

    private void createRecords(List<Record> records, Organisasjon organisasjon, Organisasjon parent, boolean update) {
        var list = new ArrayList<ToLine>();

        Optional.ofNullable(organisasjon.getNavn()).ifPresent(value -> list.add(new DetaljertNavn(value)));
        Optional.ofNullable(organisasjon.getAnsatte()).ifPresent(value -> list.add(new Ansatte(value)));
        Optional.ofNullable(organisasjon.getInternettadresse()).ifPresent(value -> list.add(new Internettadresse(value)));
        Optional.ofNullable(organisasjon.getEpost()).ifPresent(value -> list.add(new Epost(value)));
        Optional.ofNullable(organisasjon.getSektorkode()).ifPresent(value -> list.add(new Sektorkode(value)));
        Optional.ofNullable(organisasjon.getStiftelsesdato()).ifPresent(value -> list.add(new Stiftelsesdato(value)));
        Optional.ofNullable(organisasjon.getTelefon()).ifPresent(value -> list.add(new Telefon(value)));
        Optional.ofNullable(organisasjon.getNaeringskode()).ifPresent(value -> list.add(new Naeringskode(value)));
        Optional.ofNullable(organisasjon.getMaalform()).ifPresent(value -> list.add(new Maalform(value)));
        Optional.ofNullable(organisasjon.getForretningsadresse()).ifPresent(value -> list.add(new Forretningsadresse(value)));
        Optional.ofNullable(organisasjon.getPostadresse()).ifPresent(value -> list.add(new Postadresse(value)));
        Optional.ofNullable(organisasjon.getFormaal()).ifPresent(value -> list.add(new Formaal(value)));
        Optional.ofNullable(parent).ifPresent(value -> list.add(new Knytning(value, organisasjon)));

        var record = Record.create(
                list.stream().map(ToLine::toLine).collect(Collectors.toList()),
                organisasjon.getOrgnummer(),
                organisasjon.getEnhetstype(),
                update
        );

        records.add(record);
        organisasjon.getUnderenheter().forEach(value -> createRecords(records, value, organisasjon, update));
    }
}
