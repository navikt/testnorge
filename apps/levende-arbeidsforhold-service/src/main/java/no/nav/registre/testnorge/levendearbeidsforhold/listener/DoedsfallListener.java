package no.nav.registre.testnorge.levendearbeidsforhold.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.person.pdl.leesah.Personhendelse;
import no.nav.registre.testnorge.levendearbeidsforhold.service.ArbeidsforholdService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class DoedsfallListener {
    private static final String doedsfallTopic = "pdl.leesah-v1";
    @Autowired
    private final ArbeidsforholdService arbeidsforholdsService;
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent() {
        log.info("Hello World");
        String id = "18426839454";
        arbeidsforholdsService.getArbeidsforhold(id);
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
