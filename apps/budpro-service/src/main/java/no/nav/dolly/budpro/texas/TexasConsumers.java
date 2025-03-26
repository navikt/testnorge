package no.nav.dolly.budpro.texas;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

@Configuration
@ConfigurationProperties(prefix = "dolly")
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
        return consumers
                .stream()
                .filter(consumer -> consumer.getName().equals(name))
                .findFirst();
    }

}
