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

@Slf4j
public class EmbeddedApplicationStarter {
    public static void main(String[] args) {
        try {
            Map<String, Object> fasitProperties = resolveFasitProperties(args);

            new SpringApplicationBuilder()
                    .sources(ApplicationConfig.class)
                    .profiles("local")
                    .properties(fasitProperties)
                    .run(args);
        } catch (IOException e) {
            log.error("IOException - Failed to resolve fasit properties.");
        }
    }

    private static Map<String, Object> resolveFasitProperties(String... arguments) throws IOException {
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
                .run(arguments);
    }
}
