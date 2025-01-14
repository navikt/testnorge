package no.nav.testnav.identpool;

import no.nav.testnav.identpool.ajourhold.CronJobService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@Configuration
@ComponentScan(basePackages = "no.nav.testnav.identpool")
public class ComponentTestConfig {

    @MockitoBean
    protected JwtDecoder jwtDecoder;

    @MockitoBean
    protected CronJobService cronJobService;
}
