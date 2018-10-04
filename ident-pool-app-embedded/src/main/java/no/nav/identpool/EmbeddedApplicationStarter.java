package no.nav.identpool;

import java.util.Map;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import lombok.extern.slf4j.Slf4j;
import no.nav.identpool.fasit.FasitClient;
import no.nav.identpool.fasit.FasitClientApplicationConfig;

@Slf4j
public class EmbeddedApplicationStarter {
    public static void main(String[] args) {
        Map<String, Object> fasitProperties = resolveFasitProperties(args);

        new SpringApplicationBuilder()
                .sources(ApplicationConfig.class)
                .profiles("local")
                .properties(fasitProperties)
                .run(args);
    }

    private static Map<String, Object> resolveFasitProperties(String... arguments) {
        try (ConfigurableApplicationContext context = startFasitClientApplicationContext(arguments)) {
            return context.getBean(FasitClient.class).resolveFasitProperties();
        }
    }

    private static ConfigurableApplicationContext startFasitClientApplicationContext(String... arguments) {
        return new SpringApplicationBuilder()
                .sources(FasitClientApplicationConfig.class)
                .web(WebApplicationType.NONE)
                .logStartupInfo(false)
                .profiles("fasit")
                .run(arguments);
    }
}
