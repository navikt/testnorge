package no.nav.registre.orkestratoren;

import java.util.Map;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;

import no.nav.registre.orkestratoren.fasit.FasitClient;
import no.nav.registre.orkestratoren.fasit.FasitClientApplicationConfig;

@SpringBootApplication
@Profile("local")
public class LocalApplicationStarter {

    public static void main(String[] args) {

        Map<String, Object> fasitProperties = resolveFasitProperties(args);

        new SpringApplicationBuilder()
                .sources(LocalApplicationStarter.class)
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
                .bannerMode(Banner.Mode.OFF)
                .logStartupInfo(false)
                .profiles("fasit")
                .run(arguments);
    }
}
