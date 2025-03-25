package no.nav.dolly.budpro.texas;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Optional;

@Configuration
@ConfigurationProperties(prefix = "dolly")
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
@ToString
@Slf4j
class TexasConsumers {

    private List<TexasConsumer> consumers;

    public Optional<TexasConsumer> get(String name) {
        return consumers
                .stream()
                .filter(consumer -> consumer.getName().equals(name))
                .findFirst();
    }

}
