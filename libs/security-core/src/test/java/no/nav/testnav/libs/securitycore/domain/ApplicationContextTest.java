package no.nav.testnav.libs.securitycore.domain;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Disabled // Will always fail to load application context because of missing properties; run loadFailingApplicationContext() manually to verify.
@SpringBootTest
@ContextConfiguration(classes = {ApplicationContextTest.FailingProperties.class})
class ApplicationContextTest {

    @Test
    void loadFailingApplicationContext() {
        assertThat(true).isTrue();
    }

    @Configuration
    @ConfigurationProperties(prefix = "failing")
    public static class FailingProperties extends ServerProperties {
    }

}
