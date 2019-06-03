package no.nav.registre.orkestratoren.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import no.nav.registre.hodejegeren.TpsPersonDokument;
import no.nav.registre.orkestratoren.consumer.rs.HodejegerenConsumer;

@Slf4j
@Component
public class TpsPersondokumentListener {

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @JmsListener(destination = "${ork_TPS_PERSONDOKUMENT.queue.queueName}")
    public void lesFraKoe(TpsPersonDokument tpsPersonDokument) {
        log.info("Lese persondokument fra kø");
        hodejegerenConsumer.sendTpsPersondokumentTilHodejegeren(tpsPersonDokument);
    }
}
