package no.nav.testnav.identpool;

import no.nav.testnav.identpool.ajourhold.CronJobService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "no.nav.testnav.identpool")
public class ComponentTestConfig {

    @MockitoBean
    @SuppressWarnings("unused")
    protected CronJobService cronJobService;

}
