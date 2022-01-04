package no.nav.registre.syntrest.config;

import lombok.RequiredArgsConstructor;
import no.nav.registre.syntrest.consumer.SyntGetConsumer;
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
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SyntConsumerConfig {

    ///////////// URLs //////////////
    @Value("${synth-aareg-url}")
    private String aaregUrl;
    @Value("${synth-arena-meldekort-url}")
    private String arenaMeldekortUrl;
    @Value("${synth-inntekt-url}")
    private String inntektUrl;

    private final ApplicationManager applicationManager;
    private final WebClient.Builder webClientBuilder;

    // Request/Response types
    private static final ParameterizedTypeReference<List<String>> STRING_LIST_TYPE = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<List<Arbeidsforholdsmelding>> ARBEIDSFORHOLDSMELDING_LIST_TYPE = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<Map<String, List<no.nav.registre.syntrest.domain.inntekt.Inntektsmelding>>> INNTEKTSMELDING_MAP_TYPE = new ParameterizedTypeReference<>() {
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

    @Bean
    SyntGetConsumer<List<String>> meldekortConsumer() throws MalformedURLException {
        return new SyntGetConsumer<>(
                applicationManager,
                "synthdata-arena-meldekort",
                arenaMeldekortUrl,
                false,
                STRING_LIST_TYPE,
                webClientBuilder);
    }


    @Bean
    SyntPostConsumer<Map<String, List<no.nav.registre.syntrest.domain.inntekt.Inntektsmelding>>,
            Map<String, List<no.nav.registre.syntrest.domain.inntekt.Inntektsmelding>>> inntektConsumer() throws MalformedURLException {
        return new SyntPostConsumer<>(
                applicationManager,
                "synthdata-inntekt",
                inntektUrl,
                false,
                INNTEKTSMELDING_MAP_TYPE,
                INNTEKTSMELDING_MAP_TYPE,
                webClientBuilder);
    }

}
