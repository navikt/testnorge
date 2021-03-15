package no.nav.registre.testnorge.bridge.kafkaonprembridge.consumer;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.bridge.kafkaonprembridge.config.credentials.KafkaCloudBridgeServiceProperties;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Endringsdokument;
import no.nav.registre.testnorge.libs.avro.organisasjon.v1.Opprettelsesdokument;
import no.nav.registre.testnorge.libs.dto.bridge.v1.ContentDTO;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Component
public class KafkaCloudBridgeConsumer {
    private final WebClient webClient;
    private final AccessTokenService accessTokenService;
    private final NaisServerProperties serviceProperties;

    public KafkaCloudBridgeConsumer(
            KafkaCloudBridgeServiceProperties serviceProperties,
            AccessTokenService accessTokenService
    ) {
        this.accessTokenService = accessTokenService;
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    private ContentDTO toContent(String key, byte[] bytes) {
        return ContentDTO.builder().content(bytes).key(key).build();
    }

    @SneakyThrows
    public void bridge(String key, Endringsdokument endringsdokument) {
        var content = toContent(key, endringsdokument.toByteBuffer().array());
        var accessToken = accessTokenService.generateToken(serviceProperties);
        new BridgeKafkaCommand(webClient, content, accessToken.getTokenValue(), "/api/v1/organisajon/endringsdokument");
    }

    @SneakyThrows
    public void bridge(String key, Opprettelsesdokument opprettelsesdokument) {
        var content = toContent(key, opprettelsesdokument.toByteBuffer().array());
        var accessToken = accessTokenService.generateToken(serviceProperties);
        new BridgeKafkaCommand(webClient, content, accessToken.getTokenValue(), "/api/v1/organisajon/opprettelsesdokument");
    }
}
