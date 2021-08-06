package no.nav.registre.bisys.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.web.client.RestTemplate;

import no.nav.registre.bisys.consumer.rs.BisysSyntetisererenConsumer;
import no.nav.registre.bisys.service.SyntetiseringService;
import no.nav.registre.testnorge.consumers.hodejegeren.HodejegerenConsumer;
import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;

@Configuration
@Import(ApplicationCoreConfig.class)
public class AppConfig {

    @Value("${SYNTREST_REST_API}")
    private String syntrestServerUrl;

    @Value("${HODEJEGEREN_REST_API}")
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
    @DependsOn("restTemplate")
    public HodejegerenConsumer hodejegerenConsumer() {
        return new HodejegerenConsumer(hodejegerenUrl, restTemplate());
    }

    @Bean
    public SyntetiseringService syntetiseringService(
            @Autowired BisysSyntetisererenConsumer bisysSyntetisererenConsumer,
            @Value("${USE_HISTORICAL_MOTTATTDATO}") boolean useHistoricalMottattdato) {

        return SyntetiseringService.builder()
                .hodejegerenConsumer(hodejegerenConsumer())
                .bisysSyntetisererenConsumer(bisysSyntetisererenConsumer)
                .useHistoricalMottattdato(useHistoricalMottattdato).build();
    }
}
