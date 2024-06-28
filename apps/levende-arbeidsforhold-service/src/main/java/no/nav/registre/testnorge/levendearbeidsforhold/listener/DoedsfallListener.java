package no.nav.registre.testnorge.levendearbeidsforhold.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.avro.hendelse.Hendelse;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Profile("dev")
@Component
@RequiredArgsConstructor
public class DoedsfallListener {
    private static final String doedsfallTopic = "pdl.leesah-v1";


    @KafkaListener(topics = doedsfallTopic)
    public void getHendelser(List<ConsumerRecord<String, Hendelse>> records) {
        log.info(records.stream().toList().toString());

        records.stream().collect(Collectors.groupingBy(value -> value.value().getIdent())).forEach((ident, list) -> {
            var tyoe = list.stream().map(value -> value.value().getType()).toList();
            var id = list.stream().map(ConsumerRecord::key).collect(Collectors.toSet());
            log.info("ID: " + id + " Type: " + tyoe);
            if(tyoe.contains("DOEDSFALL")){
                log.info("Dødsfall for " + id);
                //Send id til en service som sletter arbeidsforhold
            }
        });
    }
}
