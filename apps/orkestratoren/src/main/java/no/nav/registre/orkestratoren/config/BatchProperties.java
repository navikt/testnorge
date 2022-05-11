package no.nav.registre.orkestratoren.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@RequiredArgsConstructor
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "batch.meldinger")
public class BatchProperties {
    private Map<String, Integer> antallSkdMeldingerPerEndringskode;
    private Map<String, Integer> antallNavMeldingerPerEndringskode;
}
