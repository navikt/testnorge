package no.nav.registre.orkestratoren.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CronJobConfig {
    @Bean
    public Map<String, Integer> antallMeldingerPerEndringskode() {
        Map<String, Integer> antallMeldingerPerEndringskode = new HashMap<>();
        return antallMeldingerPerEndringskode;
    }
}
