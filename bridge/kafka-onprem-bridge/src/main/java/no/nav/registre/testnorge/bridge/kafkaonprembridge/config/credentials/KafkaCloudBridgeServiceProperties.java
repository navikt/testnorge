package no.nav.registre.testnorge.bridge.kafkaonprembridge.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.kafka-cloud-bridge")
public class KafkaCloudBridgeServiceProperties extends NaisServerProperties {
}