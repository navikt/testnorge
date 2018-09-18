package no.nav.identpool;

import org.springframework.boot.builder.SpringApplicationBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmbeddedApplicationStarter {
    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(ApplicationConfig.class)
                .profiles("local")
                .run(args);

    }
    //        try {
    //            Map<String, Object> fasitProperties = resolveFasitProperties(args);
    //
    //            new SpringApplicationBuilder()
    //                    .sources(ApplicationConfig.class)
    //                    .profiles("local")
    //                    .properties(fasitProperties)
    //                    .run(args);
    //        } catch (IOException e) {
    //            log.error("IOException - Failed to resolve fasit properties.");
    //        }
    //    }
    //
    //    private static Map<String, Object> resolveFasitProperties(String... arguments) throws IOException {
    //        try (ConfigurableApplicationContext context = startFasitClientApplicationContext(arguments)) {
    //            return context.getBean(FasitClient.class).resolveFasitProperties();
    //        }
    //    }
    //
    //    private static ConfigurableApplicationContext startFasitClientApplicationContext(String... arguments) {
    //        return new SpringApplicationBuilder()
    //                .sources(FasitClientApplicationConfig.class)
    //                .web(WebApplicationType.NONE)
    //                .logStartupInfo(false)
    //                .bannerMode(Banner.Mode.OFF)
    //                .profiles("fasit")
    //                .properties("fasit.url=https://fasit.adeo.no", "application.name=ident-pool", "test.environment=q11", "fasit.anonymous=true")
    //                .run(arguments);
    //    }
}
