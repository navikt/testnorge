package no.nav.registre.syntrest.config;

import lombok.RequiredArgsConstructor;
import no.nav.registre.syntrest.consumer.SyntPostConsumer;
import no.nav.registre.syntrest.domain.aareg.Arbeidsforholdsmelding;
import no.nav.registre.syntrest.kubernetes.ApplicationManager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.MalformedURLException;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SyntConsumerConfig {

    ///////////// URLs //////////////
    @Value("${synth-aareg-url}")
    private String aaregUrl;

    private final ApplicationManager applicationManager;
    private final WebClient.Builder webClientBuilder;

    // Request/Response types
    private static final ParameterizedTypeReference<List<String>> STRING_LIST_TYPE = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<List<Arbeidsforholdsmelding>> ARBEIDSFORHOLDSMELDING_LIST_TYPE = new ParameterizedTypeReference<>() {
    };


    @Bean
    SyntPostConsumer<List<String>, List<Arbeidsforholdsmelding>> aaregConsumer() throws MalformedURLException {
        return new SyntPostConsumer<>(
                applicationManager,
                "synthdata-aareg",
                aaregUrl,
                false,
                STRING_LIST_TYPE,
                ARBEIDSFORHOLDSMELDING_LIST_TYPE,
                webClientBuilder);
    }

}
