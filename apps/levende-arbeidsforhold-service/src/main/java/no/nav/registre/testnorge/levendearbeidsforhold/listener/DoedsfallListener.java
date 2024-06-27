package no.nav.registre.testnorge.levendearbeidsforhold.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.avro.organisasjon.v1.Opprettelsesdokument;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactoryFriend;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import no.nav.registre.testnorge.levendearbeidsforhold.config.KafkaConfig;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.ApplicationListener;


import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@Slf4j
@Profile("dev")
@Component
@RequiredArgsConstructor
public class DoedsfallListener {
    private static final String doedsfallTopic = "pdl.leesah-v1";

    public static void helloworld(){
        System.out.println("Hello wooorld");
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onApplicationEvent() {
        log.info("Hello World");
    }
    // @KafkaListener(topics = doedsfallTopic)
    // public void getHendelser(List<ConsumerRecord<String, Object>> records) {
    //     log.info(records.stream().toList().toString());

    //     /*
    //             records.stream().collect(Collectors.groupingBy(value -> value.value().getMetadata().getMiljo())).forEach((miljo, list) -> {
    //         var organisasjoner = list.stream().map(value -> value.value().getOrganisasjon()).collect(Collectors.toList());
    //         var ids = list.stream().map(ConsumerRecord::key).collect(Collectors.toSet());
    //         save(organisasjoner, miljo, false, ids);
    //     });
    //      */
    // }
}
