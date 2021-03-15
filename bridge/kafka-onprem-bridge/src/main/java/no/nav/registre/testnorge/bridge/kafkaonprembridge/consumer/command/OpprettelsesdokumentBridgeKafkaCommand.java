package no.nav.registre.testnorge.bridge.kafkaonprembridge.consumer.command;

import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.libs.dto.bridge.v1.ContentDTO;

public class OpprettelsesdokumentBridgeKafkaCommand extends BridgeKafkaCommand {
    public OpprettelsesdokumentBridgeKafkaCommand(WebClient webClient, ContentDTO content, String token) {
        super(webClient, content, token, "/api/v1/organisajon/opprettelsesdokument");
    }
}
