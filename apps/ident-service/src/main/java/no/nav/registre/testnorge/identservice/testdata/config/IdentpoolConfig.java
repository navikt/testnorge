package no.nav.registre.testnorge.identservice.testdata.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class IdentpoolConfig {

    @Bean
    public IdentpoolConsumer identpoolConsumer() {
        return new IdentpoolConsumer();
    }
}
