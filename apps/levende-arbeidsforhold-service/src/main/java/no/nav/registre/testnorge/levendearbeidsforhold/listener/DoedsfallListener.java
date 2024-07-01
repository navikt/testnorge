package no.nav.registre.testnorge.levendearbeidsforhold.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.person.pdl.leesah.Personhendelse;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import no.nav.testnav.libs.avro.hendelse.Hendelse;

import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class DoedsfallListener {
    private static final String doedsfallTopic = "pdl.leesah-v1";

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent() {
        log.info("Hello World");
    }
/*
    @KafkaListener(topics = doedsfallTopic)
    public void getHendelser(List<ConsumerRecord<String, Personhendelse>> records) {
        for (ConsumerRecord<String, Personhendelse> record: records){
            log.info("Record key:" + record.key());
            log.info("Record value:" + record.value().toString());
        }

    }

 */
}
