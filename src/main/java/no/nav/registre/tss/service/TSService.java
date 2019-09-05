package no.nav.registre.tss.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import no.nav.registre.testnorge.consumers.HodejegerenConsumer;
import no.nav.registre.tss.domain.Person;
import no.nav.registre.tss.consumer.rs.response.TssSyntetisererenConsumer;
import no.nav.registre.tss.provider.rs.requests.SyntetiserTssRequest;

@Slf4j
@Service
public class TSService {

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    private TssSyntetisererenConsumer tssSyntetisererenConsumer;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${queue.queueName}")
    private String mqQueueName;

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
            jmsTemplate.convertAndSend(mqQueueName, message);
        }
    }
}
