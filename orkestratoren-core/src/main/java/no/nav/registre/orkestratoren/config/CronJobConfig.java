package no.nav.registre.orkestratoren.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CronJobConfig {
    @Bean
    public Map<String, Integer> antallMeldingerPerAarsakskode() {
        Map<String, Integer> antallMeldingerPerAarsakskode = new HashMap<>();
        return antallMeldingerPerAarsakskode;
    }
}
