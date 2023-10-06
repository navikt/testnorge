package no.nav.testnav.apps.organisasjontilgangservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.StreamSupport;

@Component
@Slf4j
public class ConfiguratedEnvironmentLogger {

    @EventListener
    public void handleContextRefreshedEvent(ContextRefreshedEvent event) {
        var env = (AbstractEnvironment) event.getApplicationContext().getEnvironment();
        var propertySources = env.getPropertySources();
        StreamSupport.stream(propertySources.spliterator(), false)
                .filter(EnumerablePropertySource.class::isInstance)
                .map(ps -> ((EnumerablePropertySource<?>) ps).getPropertyNames())
                .flatMap(Arrays::stream)
                .distinct()
                .sorted()
                .forEach(prop -> log.info("{}", prop));

    }

}