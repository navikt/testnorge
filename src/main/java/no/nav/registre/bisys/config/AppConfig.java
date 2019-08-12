package no.nav.registre.bisys.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.client.RestTemplate;

import no.nav.registre.bisys.consumer.rs.BisysSyntetisererenConsumer;
import no.nav.registre.bisys.consumer.ui.BisysUiConsumer;
import no.nav.registre.bisys.consumer.ui.modules.BisysUiRollerConsumer;
import no.nav.registre.bisys.consumer.ui.modules.BisysUiSakConsumer;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;

@Configuration
public class AppConfig {

    @Value("${syntrest.rest.api.url}")
    String syntrestServerUrl;

    @Value("${SYNTBISYS_USERNAME}")
    String saksbehandlerUid;

    @Value("${SYNTBISYS_PASSWORD}")
    String saksbehandlerPwd;

    @Value("${BISYS_URL}")
    String bisysUrl;

    @Value("${SAKSBEHANDLER_ROLLE}")
    String rolleSaksbehandler;

    @Value("${ENHET}")
    String enhet;

    @Value("${testnorge-hodejegeren.rest-api.url}")
    private String hodejegerenUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public BisysSyntetisererenConsumer bisysSyntetisererenConsumer() {
        return new BisysSyntetisererenConsumer(syntrestServerUrl);
    }

    @Bean
    public BisysUiConsumer bisysUiConsumer() {
        return new BisysUiConsumer(
                saksbehandlerUid, saksbehandlerPwd, bisysUrl, rolleSaksbehandler, enhet);
    }

    @Bean
    public BisysUiSakConsumer bisysUiSakConsumer() {
        return new BisysUiSakConsumer();
    }

    @Bean
    public BisysUiRollerConsumer bisysUiRollerConsumer() {
        return new BisysUiRollerConsumer();
    }

    @Bean
    @DependsOn("restTemplate")
    public HodejegerenConsumer hodejegerenConsumer() {
        return new HodejegerenConsumer(hodejegerenUrl, restTemplate());
    }
}
