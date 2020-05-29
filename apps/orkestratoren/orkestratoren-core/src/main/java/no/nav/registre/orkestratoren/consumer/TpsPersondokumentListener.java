package no.nav.registre.orkestratoren.consumer;

import static java.lang.String.format;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import no.nav.registre.orkestratoren.consumer.rs.HodejegerenHistorikkConsumer;
import no.nav.registre.orkestratoren.consumer.utils.PersondokumentConverter;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@Slf4j
@Component
public class TpsPersondokumentListener {

    @Autowired
    private PersondokumentConverter persondokumentConverter;

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    private HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;

    @Value("#{${batch.avspillergruppeId.miljoe}}")
    private Map<Long, String> avspillergruppeIdMedMiljoe;

    private Set<String> alleOrkestrerteIdenter;

    @JmsListener(destination = "${queue.queueName}")
    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "TPS" })
    public void lesFraKoe(Message message) throws JMSException {
        if (alleOrkestrerteIdenter == null) {
            log.info("Ingen identer er cachet. Henter orkestrerte identer fra hodejegeren.");
            alleOrkestrerteIdenter = new HashSet<>();
        }
        try {
            var persondokumentAsXml = ((TextMessage) message).getText();
            var tpsPersondokument = persondokumentConverter.convert(persondokumentAsXml);

            if (tpsPersondokument.getPerson().getPersonIdent().isEmpty()) {
                log.warn("Persondokument fra tps inneholder ingen identer");
            } else {
                var personIdent = tpsPersondokument.getPerson().getPersonIdent().get(0).getPersonIdent();

                sjekkOmIdentLiggerICacheOgOppdater(personIdent);

                if (alleOrkestrerteIdenter.contains(personIdent)) {
                    hodejegerenHistorikkConsumer.sendTpsPersondokumentTilHodejegeren(tpsPersondokument, personIdent);
                }
            }
        } catch (RuntimeException e) {
            log.error(format("Failed to process JMS message: [Id: %s].", message.getJMSCorrelationID()), e);
        }
    }

    private void sjekkOmIdentLiggerICacheOgOppdater(String personIdent) {
        if (!alleOrkestrerteIdenter.contains(personIdent)) {
            for (Long avspillergruppeId : avspillergruppeIdMedMiljoe.keySet()) {
                var orkestrerteIdenter = hodejegerenConsumer.get(avspillergruppeId);
                alleOrkestrerteIdenter.addAll(orkestrerteIdenter);
            }
        }
    }
}
