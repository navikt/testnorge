package no.nav.pdl.forvalter.config;

import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import no.nav.pdl.forvalter.database.JsonToPersonDTOConverter;
import no.nav.pdl.forvalter.database.PersonDTOToJsonConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.DialectResolver;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableR2dbcAuditing
@RequiredArgsConstructor
public class ReactivePostgresConfig {

    // Inject your custom converter here
    private final JsonToPersonDTOConverter jsonToPersonDTOConverter;
    private final PersonDTOToJsonConverter personDTOToJsonConverter;

    @Bean
    public R2dbcCustomConversions customConversions(ConnectionFactory connectionFactory) {
        List<Object> converters = new ArrayList<>();
        converters.add(jsonToPersonDTOConverter);
        converters.add(personDTOToJsonConverter);
        // Add other custom converters if needed

        return R2dbcCustomConversions.of(DialectResolver.getDialect(connectionFactory), converters);
    }
}