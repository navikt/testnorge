package no.nav.testnav.mocks.tokendings;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.config.EnableWebFlux;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Import({
        CoreConfig.class,
})
@EnableWebFlux
@SpringBootApplication
public class TokendingsMockApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(TokendingsMockApplicationStarter.class, args);
    }

    @UtilityClass
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
