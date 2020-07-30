package no.nav.registre.testnorge.arbeidsforhold.consumer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import no.nav.registre.testnorge.avro.hendelse.Hendelse;
import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.dependencyanalysis.DependencyType;
import no.nav.registre.testnorge.dto.hendelse.v1.HendelseType;

@Component
@DependencyOn(value = "hendelse-api", type = DependencyType.QUEUE)
public class HendelseConsumer {

    public HendelseConsumer(@Value("${kafka.topic}") String topic, KafkaTemplate<String, Hendelse> kafkaTemplate) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    private final String topic;
    private final KafkaTemplate<String, Hendelse> kafkaTemplate;

    public void registerArbeidsforholdOpprettetHendelse(String ident, LocalDate fom, LocalDate tom) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        kafkaTemplate.send(
                topic,
                Hendelse.newBuilder()
                        .setIdent(ident)
                        .setFom(fom.format(formatter))
                        .setTom(tom.format(formatter))
                        .setType(HendelseType.ARBEIDSFORHOLD_OPPRETTET.name())
                        .build()
        );
    }
}
