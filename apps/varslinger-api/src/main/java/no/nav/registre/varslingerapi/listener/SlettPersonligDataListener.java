package no.nav.registre.varslingerapi.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.libs.avro.personinfo.PersonInfo;
import no.nav.registre.varslingerapi.adapter.PersonVarslingAdapter;

@Slf4j
@Component
@Profile("prod")
@RequiredArgsConstructor
public class SlettPersonligDataListener {
    private final PersonVarslingAdapter personVarslingAdapter;

    @KafkaListener(topics = "testnorge-slett-personlig-data-v1")
    public void register(@Payload PersonInfo personInfo) {
        log.info("Sletter personlig data for {}", personInfo.getObjectId());
        personVarslingAdapter.delete(personInfo);
    }
}