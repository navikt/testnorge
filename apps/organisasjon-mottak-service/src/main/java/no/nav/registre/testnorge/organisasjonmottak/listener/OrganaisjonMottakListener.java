package no.nav.registre.testnorge.organisasjonmottak.listener;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Endringsdokument;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Opprettelsesdokument;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Organisasjon;
import no.nav.registre.testnorge.libs.kafkaconfig.topic.OrganisasjonTopic;
import no.nav.registre.testnorge.organisasjonmottak.consumer.EregConsumer;
import no.nav.registre.testnorge.organisasjonmottak.domain.Ansatte;
import no.nav.registre.testnorge.organisasjonmottak.domain.DetaljertNavn;
import no.nav.registre.testnorge.organisasjonmottak.domain.Epost;
import no.nav.registre.testnorge.organisasjonmottak.domain.Flatfil;
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
    private final EregConsumer eregConsumer;


    @KafkaListener(topics = OrganisasjonTopic.ORGANISASJON_OPPRETT_ORGANISASJON)
    public void create(ConsumerRecord<String, Opprettelsesdokument> record) {
        var key = record.key();
        var organisasjon = record.value().getOrganisasjon();
        log.info("Oppretter ny organisasisjon {} med uuid: {}.", organisasjon.getOrgnummer(), key);
        save(key, record.value().getMetadata().getMiljo(), organisasjon, false);
    }

    @KafkaListener(topics = OrganisasjonTopic.ORGANISASJON_ENDRE_ORGANISASJON)
    public void update(ConsumerRecord<String, Endringsdokument> record) {
        var key = record.key();
        var organisasjon = record.value().getOrganisasjon();
        log.info("Endrer organisasisjon {} med uuid: {}.", organisasjon.getOrgnummer(), key);
        save(key, record.value().getMetadata().getMiljo(), organisasjon, true);
    }

    private void save(String uuid, String miljo, Organisasjon organisasjon, boolean update) {
        var list = new ArrayList<ToLine>();
        list.add(new DetaljertNavn(organisasjon.getNavn()));

        Optional.of(organisasjon.getAnsatte()).ifPresent(value -> list.add(new Ansatte(value)));
        Optional.of(organisasjon.getInternettadresse()).ifPresent(value -> list.add(new Internettadresse(value)));
        Optional.of(organisasjon.getEpost()).ifPresent(value -> list.add(new Epost(value)));
        Optional.of(organisasjon.getSektorkode()).ifPresent(value -> list.add(new Sektorkode(value)));
        Optional.of(organisasjon.getStiftelsesdato()).ifPresent(value -> list.add(new Stiftelsesdato(value)));
        Optional.of(organisasjon.getTelefon()).ifPresent(value -> list.add(new Telefon(value)));
        Optional.of(organisasjon.getNaeringskode()).ifPresent(value -> list.add(new Naeringskode(value)));
        Optional.of(organisasjon.getMaalform()).ifPresent(value -> list.add(new Maalform(value)));
        Optional.of(organisasjon.getKnytning()).ifPresent(value -> list.add(new Knytning(value, organisasjon)));
        Optional.of(organisasjon.getForretningsadresse()).ifPresent(value -> list.add(new Forretningsadresse(value)));
        Optional.of(organisasjon.getPostadresse()).ifPresent(value -> list.add(new Postadresse(value)));

        var record = Record.create(
                list.stream().map(ToLine::toLine).collect(Collectors.toList()),
                organisasjon.getOrgnummer(),
                organisasjon.getEnhetstype(),
                update
        );
        eregConsumer.save(Flatfil.create(record), miljo, uuid);
    }
}
