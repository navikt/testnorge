package no.nav.identpool.fasit;

import java.net.URI;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.val;

@Configuration
public class FasitServiceConfig {

    @Bean
    public RestConsumer consumer() {
        val template = new RestTemplateBuilder()
                .build();
        return url -> template.getForEntity(URI.create(url), String.class).getBody();
    }
}