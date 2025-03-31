package no.nav.testnav.apps.tpsmessagingservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tpsmessagingservice.consumer.XmlMeldingConsumer;
import no.nav.testnav.apps.tpsmessagingservice.exception.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class XmlService {

    private static final List<String> MILJOER = List.of("D8", "T3", "Q1", "Q2", "Q4");
    private static final List<String> QUEUES =
            List.of("QA.<miljø>_411.TPS_FORESPORSEL_XML_O",
                    "QA.<miljø>_411.TPS_FORESPORSEL_INFO");

    private final XmlMeldingConsumer xmlMeldingConsumer;

    public String sendXml(String xml, String queue) {

        validateQueue(queue);
        return xmlMeldingConsumer.sendMessage(xml, queue);
    }

    public List<String> getQueues() {

        return QUEUES.stream()
                .flatMap(queue -> MILJOER.stream().map(miljoe -> queue.replace("<miljø>", miljoe)))
                .sorted()
                .toList();
    }

    private void validateQueue(String queue) {

        if (MILJOER.stream()
                .noneMatch(miljoe -> queue.matches("QA\\.%s_[0-9,A-Z_.]+".formatted(miljoe)))) {

            throw new BadRequestException("Kønavn %s er ikke gyldig!".formatted(queue));
        }
    }
}
