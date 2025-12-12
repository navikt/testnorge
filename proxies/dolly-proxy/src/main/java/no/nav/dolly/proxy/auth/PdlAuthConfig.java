package no.nav.dolly.proxy.auth;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class PdlAuthConfig {

    @Value("${app.pdl.elastic.username}")
    private String elasticUsername;

    @Value("${app.pdl.elastic.password}")
    private String elasticPassword;

    @Value("${app.pdl.hendelse.apikey}")
    private String hendelseApiKey;

}
