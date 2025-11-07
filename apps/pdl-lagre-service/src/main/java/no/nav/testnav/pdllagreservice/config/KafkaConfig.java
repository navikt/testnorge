package no.nav.testnav.pdllagreservice.config;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.val;
import no.nav.testnav.pdllagreservice.kafka.InternalKafkaController;
import no.nav.testnav.pdllagreservice.kafka.KafkaBeanUtilities;
import no.nav.testnav.pdllagreservice.kafka.KafkaConsumerHealthIndicator;
import no.nav.testnav.pdllagreservice.kafka.KafkaContainerUtils;
import no.nav.testnav.pdllagreservice.kafka.KafkaHealthIndicator;
import no.nav.testnav.pdllagreservice.kafka.RestartingErrorHandler;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableKafka
@EnableScheduling
@Configuration(proxyBeanMethods = false)
class KafkaConfig {

    @Bean
    RestartingErrorHandler restartingErrorHandler(KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry, MeterRegistry meterRegistry) {
        return new RestartingErrorHandler(kafkaListenerEndpointRegistry, meterRegistry);
    }

    @Bean
    public AdminClient kafkaAdminClient(KafkaAdmin kafkaAdmin) {
        return AdminClient.create(kafkaAdmin.getConfigurationProperties());
    }

//    @Bean
//    HealthIndicator kafkaHealthIndicator(AdminClient adminClient) {
//        return new KafkaHealthIndicator(adminClient);
//    }

    @Bean
    KafkaConsumerHealthIndicator KafkaConsumerHealthIndicator(KafkaListenerEndpointRegistry listenerEndpointRegistry) {
        return new KafkaConsumerHealthIndicator(listenerEndpointRegistry);
    }

    @Bean
    InternalKafkaController internalKafkaController() {
        return new InternalKafkaController();
    }

    @Bean("pdlDokumentKafkaFactory")
    ConcurrentKafkaListenerContainerFactory<?, ?> pdlDokumentKafkaListenerContainerFactory(
            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
            ConsumerFactory<Object, Object> kafkaConsumerFactory) {

        val consumerFactory = KafkaBeanUtilities.stringStringConsumerFactory(kafkaConsumerFactory.getConfigurationProperties());
        return KafkaContainerUtils.containerFactory(configurer, consumerFactory, 1000);
    }
}
