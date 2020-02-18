package no.nav.registre.spion.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;


@ComponentScan
@EnableKafka
@Configuration
public class KafkaConfig {
}