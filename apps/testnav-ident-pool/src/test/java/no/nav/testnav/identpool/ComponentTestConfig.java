package no.nav.testnav.identpool;

import no.nav.testnav.identpool.ajourhold.CronJobService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@Configuration
@ComponentScan(basePackages = "no.nav.testnav.identpool")
public class ComponentTestConfig {

    @MockBean
    protected JwtDecoder jwtDecoder;

    @MockBean
    protected CronJobService cronJobService;
}
