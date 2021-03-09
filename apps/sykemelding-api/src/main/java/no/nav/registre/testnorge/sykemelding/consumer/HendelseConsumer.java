package no.nav.registre.testnorge.sykemelding.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import no.nav.registre.testnorge.libs.avro.hendelse.Hendelse;
import no.nav.registre.testnorge.libs.dto.hendelse.v1.HendelseType;

@Component
public class HendelseConsumer {

    public HendelseConsumer(@Value("${kafka.topic}") String topic, KafkaTemplate<String, Hendelse> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    private final String topic;
    private final KafkaTemplate<String, Hendelse> kafkaTemplate;

    public void registerSykemeldingOpprettetHendelse(String ident, LocalDate fom, LocalDate tom) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        kafkaTemplate.send(
                topic,
                Hendelse.newBuilder()
                        .setIdent(ident)
                        .setFom(fom.format(formatter))
                        .setTom(tom.format(formatter))
                        .setType(HendelseType.SYKEMELDING_OPPRETTET.name())
                        .build()
        );
    }
}
