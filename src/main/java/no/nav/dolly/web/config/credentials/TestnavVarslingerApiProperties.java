package no.nav.dolly.web.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnorge-varslinger-api")
public class TestnavVarslingerApiProperties extends NaisServerProperties {
}