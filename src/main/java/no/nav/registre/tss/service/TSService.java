package no.nav.registre.tss.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import no.nav.registre.testnorge.consumers.HodejegerenConsumer;
import no.nav.registre.tss.consumer.rs.response.TssSyntetisererenConsumer;
import no.nav.registre.tss.domain.Person;
import no.nav.registre.tss.provider.rs.requests.SyntetiserTssRequest;
import no.nav.registre.tss.utils.Rutine910Util;

@Slf4j
@Service
public class TSService {

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    private TssSyntetisererenConsumer tssSyntetisererenConsumer;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${queue.queueNameAjourhold}")
    private String mqQueueNameAjourhold;

    @Value("${queue.queueNameSamhandlerService}")
    private String mqQueueNameSamhandlerService;

    public List<Person> getIds(SyntetiserTssRequest syntetiserTssRequest) {
        Map<String, JsonNode> personer = hodejegerenConsumer.getStatusQuo(
                syntetiserTssRequest.getAvspillergruppeId(),
                syntetiserTssRequest.getMiljoe(),
                syntetiserTssRequest.getAntallNyeIdenter(),
                25,
                60);

        List<Person> personList = new ArrayList<>(personer.size());
        for (Map.Entry<String, JsonNode> entry : personer.entrySet()) {
            JsonNode jsonNode = entry.getValue();
            personList.add(new Person(
                    entry.getKey(),
                    jsonNode.findValue("gjeldendePersonnavn").asText()));
        }
        return personList;
    }

    public List<String> getMessagesFromSynt(List<Person> personer) {
        return tssSyntetisererenConsumer.produceFilesToTSS(personer);
    }

    public void sendToMQQueue(List<String> messages) {
        for (String message : messages) {
            log.info("Sender melding til TSS: {}", message);
            jmsTemplate.convertAndSend("queue:///" + mqQueueNameAjourhold + "?targetClient=1", message);
        }
    }

    public void sendAndReceiveFromTss(Long avspillergruppeId, Integer antallLeger) {
        List<String> alleLeger = hodejegerenConsumer.getLevende(avspillergruppeId);
        List<String> utvalgteLeger = new ArrayList<>();
        if (antallLeger != null) {
            Collections.shuffle(alleLeger);
            utvalgteLeger.addAll(alleLeger.subList(0, antallLeger));
        } else {
            utvalgteLeger.addAll(alleLeger);
        }

        for (String lege : utvalgteLeger) {
            String rutine910 = Rutine910Util.opprettRutine(lege);

            Message received = jmsTemplate.sendAndReceive("queue:///" + mqQueueNameSamhandlerService + "?targetClient=1", session -> {
                //            String msgId = "foo";
                TextMessage message = session.createTextMessage(rutine910);
                //            message.setJMSCorrelationID(msgId);
                return message;
            });

            try {
                if (received != null) {
                    log.info(received.getBody(String.class));
                } else {
                    log.warn("Fikk ikke svar");
                }
            } catch (JMSException e) {
                log.error("Kunne ikke hente body", e);
            }
        }
    }

    public void sendAndReceiveFromTss(String lege) {
        String rutine910 = Rutine910Util.opprettRutine(lege);

        Message received = jmsTemplate.sendAndReceive("queue:///" + mqQueueNameSamhandlerService + "?targetClient=1", session -> {
            //            String msgId = "foo";
            TextMessage message = session.createTextMessage(rutine910);
            //            message.setJMSCorrelationID(msgId);
            return message;
        });

        try {
            if (received != null) {
                log.info(received.getBody(String.class));
            } else {
                log.warn("Fikk ikke svar");
            }
        } catch (JMSException e) {
            log.error("Kunne ikke hente body", e);
        }
    }
}
