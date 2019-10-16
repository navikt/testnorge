package no.nav.dolly.local;

import no.nav.dolly.ApplicationConfig;
import no.nav.dolly.local.fasit.FasitClient;
import no.nav.dolly.local.fasit.FasitClientApplicationConfig;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Map;

public class LocalApplicationStarter {
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
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static ConfigurableApplicationContext startFasitClientApplicationContext(String... arguments) {
        return new SpringApplicationBuilder()
                .sources(FasitClientApplicationConfig.class)
                .web(WebApplicationType.NONE)
                .logStartupInfo(true)
                .bannerMode(Banner.Mode.OFF)
                .profiles("fasit")
                .properties("fasit.url=https://fasit.adeo.no", "application.name=dolly")
                .run(arguments);
    }
}
