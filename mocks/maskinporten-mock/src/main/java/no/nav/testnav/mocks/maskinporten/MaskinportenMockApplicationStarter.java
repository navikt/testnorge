package no.nav.testnav.mocks.maskinporten;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.config.EnableWebFlux;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Import({
        CoreConfig.class,
})
@EnableWebFlux
@SpringBootApplication
public class MaskinportenMockApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(MaskinportenMockApplicationStarter.class, args);
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Utils {

        @SneakyThrows
        public static String loadJson(String path) {
            try (final InputStreamReader stream = new InputStreamReader(new ClassPathResource(path).getInputStream(), StandardCharsets.UTF_8)) {
                return new BufferedReader(stream)
                        .lines()
                        .collect(Collectors.joining("\n"));
            }
        }

    }

}
