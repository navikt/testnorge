package no.nav.registre.testnorge.originalpopulasjon.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import no.nav.registre.testnorge.libs.avro.person.Adresse;
import no.nav.registre.testnorge.libs.avro.person.Person;
import no.nav.testnav.libs.dto.person.v1.AdresseDTO;
import no.nav.testnav.libs.dto.person.v1.PersonDTO;

@Slf4j
@Component
public class HendelseConsumer {

    public HendelseConsumer(@Value("${kafka.topic}") String topic, KafkaTemplate<String, Person> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    private final String topic;
    private final KafkaTemplate<String, Person> kafkaTemplate;

    public void registrertOpprettelseAvPerson(PersonDTO personDTO) {
        AdresseDTO adresseDTO = personDTO.getAdresse();
        var adresse = adresseDTO == null
                ? null
                : Adresse.newBuilder()
                .setGatenavn(adresseDTO.getGatenavn())
                .setPostnummer(adresseDTO.getPostnummer())
                .setPoststed(adresseDTO.getPoststed())
                .setKommunenummer(adresseDTO.getKommunenummer())
                .build();

        log.info("Sender person {} til kafkakø", personDTO.getIdent());
        kafkaTemplate.send(
                topic,
                Person.newBuilder()
                        .setIdent(personDTO.getIdent())
                        .setFornavn(personDTO.getFornavn())
                        .setMellomnavn(personDTO.getMellomnavn() == null ? "" : personDTO.getMellomnavn())
                        .setEtternavn(personDTO.getEtternavn())
                        .setFoedselsdato(personDTO.getFoedselsdato().toString())
                        .setAdresse(adresse)
                        .setTags(new ArrayList<>(personDTO.getTags()))
                        .build()
        );
    }
}
