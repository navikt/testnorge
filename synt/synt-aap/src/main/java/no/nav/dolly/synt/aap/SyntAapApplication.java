package no.nav.dolly.synt.aap;

import lombok.Getter;
import lombok.Setter;
import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@SpringBootApplication
public class SyntAapApplication {

    static void main(String[] args) {
        new SpringApplicationBuilder(SyntAapApplication.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }

    @Component
    @ConfigurationProperties(prefix = "app.config")
    @Getter
    @Setter
    public static class Config {

        private String bucket;
        private List<String> models;

    }

}

