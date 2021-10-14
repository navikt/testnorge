package no.nav.testnav.apps.brukerservice.initializer;

import io.r2dbc.h2.H2ConnectionFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class H2Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        applicationContext
                .getBeanFactory()
                .registerSingleton("connectionFactory", H2ConnectionFactory.inMemory("testdb"));
    }
}
