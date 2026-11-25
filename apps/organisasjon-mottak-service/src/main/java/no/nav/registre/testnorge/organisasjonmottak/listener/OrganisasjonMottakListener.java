package no.nav.registre.testnorge.organisasjonmottak.listener;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.organisasjonmottak.consumer.EregDollyEnheterConsumer;
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
import no.nav.testnav.libs.avro.organisasjon.v1.Endringsdokument;
import no.nav.testnav.libs.avro.organisasjon.v1.Opprettelsesdokument;
import no.nav.testnav.libs.avro.organisasjon.v1.Organisasjon;
import no.nav.testnav.libs.kafkaconfig.topic.v2.OrganisasjonTopic;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Profile("prod")
@Component
@RequiredArgsConstructor
public class OrganisasjonMottakListener {
    private final EregDollyEnheterConsumer eregDollyEnheterConsumer;

    private record Dokument(String miljo, Organisasjon organisasjon, boolean update) {
    }

    @KafkaListener(topics = {
            OrganisasjonTopic.ORGANISASJON_OPPRETT_ORGANISASJON,
            OrganisasjonTopic.ORGANISASJON_ENDRE_ORGANISASJON
    })
    public void listen(List<ConsumerRecord<String, SpecificRecord>> records) {
        Flux.fromIterable(records.stream()
                        .map(ConsumerRecord::value)
                        .map(OrganisasjonMottakListener::tilDokument)
                        .flatMap(Optional::stream)
                        .sorted(Comparator.comparing(Dokument::update))
                        .collect(Collectors.groupingBy(Dokument::miljo, LinkedHashMap::new, Collectors.toList()))
                        .entrySet())
                .flatMap(entry -> save(entry.getValue(), entry.getKey()))
                .blockLast();
    }

    private static Optional<Dokument> tilDokument(SpecificRecord value) {
        if (value instanceof Opprettelsesdokument opprettelse) {
            return Optional.of(new Dokument(opprettelse.getMetadata().getMiljo(), opprettelse.getOrganisasjon(), false));
        }
        if (value instanceof Endringsdokument endring) {
            return Optional.of(new Dokument(endring.getMetadata().getMiljo(), endring.getOrganisasjon(), true));
        }
        log.warn("Ukjent dokumenttype {} ble ignorert.", isNull(value) ? null : value.getClass().getName());
        return Optional.empty();
    }

    private Mono<Void> save(List<Dokument> dokumenter, String miljo) {
        var lines = dokumenter
                .stream()
                .map(dokument -> createRecords(dokument.organisasjon(), dokument.update()))
                .flatMap(Collection::stream)
                .toList();

        return eregDollyEnheterConsumer.send(Flatfil.create(lines), miljo);
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

        var stiftelsesDato = (nonNull(organisasjon.getStiftelsesdato()) && nonNull(organisasjon.getStiftelsesdato().getDato()) ? organisasjon.getStiftelsesdato().getDato() : null);
        var regDato = (nonNull(organisasjon.getStiftelsesdato())
                //Registreringsdato settes til dagen etter stiftelsesdato hvis den er satt
                ? Date.from(LocalDate.of(stiftelsesDato.getAar(), stiftelsesDato.getMaaned(), stiftelsesDato.getDag())
                .plusDays(1).atStartOfDay()
                .toInstant(ZoneOffset.UTC))
                : null);

        var record = Record.create(
                list.stream().map(ToLine::toLine)
                        .toList(),
                organisasjon.getOrgnummer(),
                organisasjon.getEnhetstype(),
                regDato,
                update
        );

        records.add(record);
        organisasjon.getUnderenheter().forEach(value -> createRecords(records, value, organisasjon, update));
    }
}
