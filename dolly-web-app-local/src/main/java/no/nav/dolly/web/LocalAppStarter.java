package no.nav.dolly.web;

import org.springframework.boot.builder.SpringApplicationBuilder;

public class LocalAppStarter {
    public static void main(String... arguments) {
        // todo find vault alternative
        //        Map<String, Object> fasitProperties = resolveFasitProperties(arguments);

        new SpringApplicationBuilder()
                .sources(ApplicationConfig.class)
                .profiles("local")
                //                .properties(fasitProperties)
                .run(arguments);
    }
}
