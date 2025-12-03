package no.nav.dolly.proxy.auth;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PdlAuthConfig {

    @Value("${app.pdl.elastic.username}")
    @Getter
    private String elasticUsername;

    @Value("${app.pdl.elastic.password}")
    @Getter
    private String elasticPassword;

    @Value("${app.pdl.hendelse.apikey}")
    @Getter
    private String hendelseApiKey;

}
