package no.nav.identpool;

import java.io.IOException;
import java.util.Map;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import lombok.extern.slf4j.Slf4j;

import no.nav.identpool.fasit.FasitClient;
import no.nav.identpool.fasit.FasitClientApplicationConfig;

public class EmbeddedApplicationStarter {

    public static void main(String... arguments) {
        Map<String, Object> fasitProperties = resolveFasitProperties(arguments);

        new SpringApplicationBuilder()
                .sources(ApplicationConfig.class)
                .profiles("local")
                .properties(fasitProperties)
                .run(arguments);
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
                .bannerMode(Banner.Mode.OFF)
                .profiles("fasit")
                .properties("fasit.url=https://fasit.adeo.no", "application.name=tps-forvalteren")
                .run(arguments);
    }
}
