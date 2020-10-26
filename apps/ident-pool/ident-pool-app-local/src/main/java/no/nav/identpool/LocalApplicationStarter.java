package no.nav.identpool;

import org.springframework.boot.builder.SpringApplicationBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalApplicationStarter {
    public static void main(String[] args) {

        new SpringApplicationBuilder()
                .sources(ApplicationConfig.class)
                .profiles("local")
                .run(args);
    }
}
