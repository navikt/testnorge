package no.nav.testnav.apps.tpsmessagingservice.service;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.tpsmessagingservice.consumer.TpsConsumer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class XmlService {

    private final TpsConsumer tpsConsumer;

    public String sendXml(String miljoe, String queue, String xml) {

        return tpsConsumer.sendMessage(xml, miljoe);
    }

    public List<String> getQueues() {

        return List.of("kønavn1","kønavn2");
    }
}
