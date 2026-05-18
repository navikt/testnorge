package no.nav.testnav.pdllagreservice.config;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.val;
import no.nav.testnav.pdllagreservice.kafka.KafkaBeanUtilities;
import no.nav.testnav.pdllagreservice.kafka.KafkaContainerUtils;
import no.nav.testnav.pdllagreservice.kafka.RestartingErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableKafka
@EnableScheduling
@Configuration(proxyBeanMethods = false)
class KafkaConfig {

    @Bean
    public RestartingErrorHandler restartingErrorHandler(KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry, MeterRegistry meterRegistry) {
        return new RestartingErrorHandler(kafkaListenerEndpointRegistry, meterRegistry);
    }

    @Bean("pdlDokumentKafkaFactory")
    public ConcurrentKafkaListenerContainerFactory<?, ?> pdlDokumentKafkaListenerContainerFactory(
            ConsumerFactory<Object, Object> kafkaConsumerFactory) {

        val consumerFactory = KafkaBeanUtilities.stringStringConsumerFactory(kafkaConsumerFactory.getConfigurationProperties());
        return KafkaContainerUtils.containerFactory(consumerFactory, 1000);
    }

    @Bean("pdlAdresseKafkaFactory")
    public ConcurrentKafkaListenerContainerFactory<?, ?> adresseKafkaListenerContainerFactory(
            ConsumerFactory<Object, Object> kafkaConsumerFactory) {

        val consumerFactory = KafkaBeanUtilities.longStringConsumerFactory(kafkaConsumerFactory.getConfigurationProperties());
        return KafkaContainerUtils.containerFactory(consumerFactory, 1000);
    }
}