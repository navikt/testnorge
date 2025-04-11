package no.nav.dolly.libs.texas;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Configuration
@ConfigurationProperties(prefix = "dolly.texas")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
@Setter
@ToString
@Slf4j
class TexasConsumers {

    /**
     * List of consumers intended for use with Texas.
     */
    private List<TexasConsumer> consumers;

    public Optional<TexasConsumer> get(String name) {
        return Optional
                .ofNullable(consumers)
                .orElse(Collections.emptyList())
                .stream()
                .filter(consumer -> consumer.getName().equals(name))
                .findFirst();
    }

}
