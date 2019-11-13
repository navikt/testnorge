package no.nav.registre.orkestratoren.consumer;

import static java.lang.String.format;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.rtv.namespacetps.TpsPersonDokumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.nav.registre.orkestratoren.consumer.rs.HodejegerenConsumer;
import no.nav.registre.orkestratoren.consumer.utils.PersondokumentConverter;

@Slf4j
@Component
public class TpsPersondokumentListener {

    @Autowired
    private PersondokumentConverter persondokumentConverter;

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

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
            String persondokumentAsXml = ((TextMessage) message).getText();
            TpsPersonDokumentType tpsPersondokument = persondokumentConverter.convert(persondokumentAsXml);

            if (tpsPersondokument.getPerson().getPersonIdent().isEmpty()) {
                log.warn("Persondokument fra tps inneholder ingen identer");
            } else {
                String personIdent = tpsPersondokument.getPerson().getPersonIdent().get(0).getPersonIdent();

                sjekkOmIdentLiggerICacheOgOppdater(personIdent);

                if (alleOrkestrerteIdenter.contains(personIdent)) {
                    hodejegerenConsumer.sendTpsPersondokumentTilHodejegeren(tpsPersondokument, personIdent);
                    log.info("Sendte ident {} til hodejegeren", personIdent);
                }
            }
        } catch (RuntimeException e) {
            log.error(format("Failed to process JMS message: [Id: %s].", message.getJMSCorrelationID()), e);
        }
    }

    private void sjekkOmIdentLiggerICacheOgOppdater(String personIdent) {
        if (!alleOrkestrerteIdenter.contains(personIdent)) {
            for (Long avspillergruppeId : avspillergruppeIdMedMiljoe.keySet()) {
                List<String> orkestrerteIdenter = hodejegerenConsumer.hentAlleIdenter(avspillergruppeId);
                alleOrkestrerteIdenter.addAll(orkestrerteIdenter);
            }
        }
    }
}
