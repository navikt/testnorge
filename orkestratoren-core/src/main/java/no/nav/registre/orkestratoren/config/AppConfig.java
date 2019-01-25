package no.nav.registre.orkestratoren.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.web.client.RestTemplate;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;

import no.nav.registre.orkestratoren.batch.JobController;
import no.nav.registre.orkestratoren.consumer.rs.TpsfConsumer;
import no.nav.registre.orkestratoren.provider.rs.InternalController;

@Configuration
@Import({ InternalController.class, JobController.class, TpsfConsumer.class })
public class AppConfig {

    @Value("${testnorges.ida.credential.tpsf.username}")
    private String tpsfUsername;

    @Value("${testnorges.ida.credential.tpsf.password}")
    private String tpsfPassword;

    @Bean
    public RestTemplate restTemplateTpsf() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(tpsfUsername, tpsfPassword));
        return restTemplate;
    }

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
}
