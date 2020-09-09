package no.nav.registre.testnorge.hendelse.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import no.nav.registre.testnorge.libs.avro.hendelse.Hendelse;
import no.nav.registre.testnorge.hendelse.adapter.HendelseAdapter;


@Slf4j
@Component
@Profile("prod")
@RequiredArgsConstructor
public class OpprettHendelseListener {

    private final HendelseAdapter adapter;

    @KafkaListener(topics = "testnorge-opprett-hendelse-v1")
    public void register(@Payload Hendelse hendelse) {
        adapter.opprett(new no.nav.registre.testnorge.hendelse.domain.Hendelse(hendelse));
    }
}