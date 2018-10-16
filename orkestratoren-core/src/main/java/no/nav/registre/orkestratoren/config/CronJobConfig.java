package no.nav.registre.orkestratoren.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CronJobConfig {
    @Bean
    public Map<String, Integer> antallMeldingerPerAarsakskode() {
        Map<String, Integer> antallMeldingerPerAarsakskode = new HashMap<>();
        return antallMeldingerPerAarsakskode;
    }
}
