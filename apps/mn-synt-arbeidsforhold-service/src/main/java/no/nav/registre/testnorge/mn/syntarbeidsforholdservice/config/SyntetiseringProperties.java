package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "syntetisering")
@Getter
@Setter
public class SyntetiseringProperties {
    private Float endringssannsynlighet;
}
