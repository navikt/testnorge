package no.nav.registre.syntrest.config;

import lombok.RequiredArgsConstructor;
import no.nav.registre.syntrest.consumer.SyntGetConsumer;
import no.nav.registre.syntrest.consumer.SyntPostConsumer;
import no.nav.registre.syntrest.consumer.domain.SyntAmeldingConsumer;
import no.nav.registre.syntrest.domain.aareg.Arbeidsforholdsmelding;
import no.nav.registre.syntrest.domain.bisys.Barnebidragsmelding;
import no.nav.registre.syntrest.domain.frikort.FrikortKvittering;
import no.nav.registre.syntrest.domain.inst.Institusjonsmelding;
import no.nav.registre.syntrest.domain.medl.Medlemskapsmelding;
import no.nav.registre.syntrest.domain.popp.Inntektsmelding;
import no.nav.registre.syntrest.domain.sam.SamMelding;
import no.nav.registre.syntrest.domain.tp.TPmelding;
import no.nav.registre.syntrest.domain.tps.SkdMelding;
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
    @Value("${synth-arena-bisys-url}")
    private String bisysUrl;
    @Value("${synth-inst-url}")
    private String instUrl;
    @Value("${synth-medl-url}")
    private String medlUrl;
    @Value("${synth-arena-meldekort-url}")
    private String arenaMeldekortUrl;
    @Value("${synth-nav-url}")
    private String navEndringsmeldingUrl;
    @Value("${synth-popp-url}")
    private String poppUrl;
    @Value("${synth-sam-url}")
    private String samUrl;
    @Value("${synth-inntekt-url}")
    private String inntektUrl;
    @Value("${synth-tp-url}")
    private String tpUrl;
    @Value("${synth-tps-url}")
    private String tpsUrl;
    @Value("${synth-frikort-url}")
    private String frikortUrl;
    @Value("${synth-amelding-url}")
    private String ameldingUrl;

    private final ApplicationManager applicationManager;
    private final WebClient.Builder webClientBuilder;

    // Request/Response types
    private static final ParameterizedTypeReference<List<String>> STRING_LIST_TYPE = new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<Map<String, Integer>> STRING_INTEGER_MAP_TYPE = new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<List<Arbeidsforholdsmelding>> ARBEIDSFORHOLDSMELDING_LIST_TYPE = new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<List<Barnebidragsmelding>> BARNEBIDRAGSMELDING_LIST_TYPE = new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<List<Institusjonsmelding>> INSTITUSJONSMELDING_LIST_TYPE = new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<List<Medlemskapsmelding>> MEDLEMSKAPSMELDING_LIST_TYPE = new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<List<Inntektsmelding>> INNTEKTSMELDING_POPP_LIST_TYPE = new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<List<SamMelding>> SAM_MELDING_LIST_TYPE = new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<Map<String, List<no.nav.registre.syntrest.domain.inntekt.Inntektsmelding>>> INNTEKTSMELDING_MAP_TYPE = new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<List<TPmelding>> TP_MELDING_LIST_TYPE = new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<List<SkdMelding>> SKD_MELDING_LIST_TYPE = new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<Map<String, List<FrikortKvittering>>> STRING_FRIKORT_KVITTERING_MAP_TYPE = new ParameterizedTypeReference<>() {};


    @Bean
    SyntPostConsumer<List<String>, List<Arbeidsforholdsmelding>> aaregConsumer() throws MalformedURLException {
        return new SyntPostConsumer<>(
                applicationManager,
                "synthdata-aareg",
                aaregUrl,
                true,
                STRING_LIST_TYPE,
                ARBEIDSFORHOLDSMELDING_LIST_TYPE,
                webClientBuilder);
    }

    @Bean
    SyntGetConsumer<List<Barnebidragsmelding>> bisysConsumer() throws MalformedURLException {
        return new SyntGetConsumer<>(
                applicationManager,
                "synthdata-arena-bisys",
                bisysUrl,
                true,
                BARNEBIDRAGSMELDING_LIST_TYPE,
                webClientBuilder);
    }

    @Bean
    SyntGetConsumer<List<Institusjonsmelding>> instConsumer() throws MalformedURLException {
        return new SyntGetConsumer<>(
                applicationManager,
                "synthdata-inst",
                instUrl,
                true,
                INSTITUSJONSMELDING_LIST_TYPE,
                webClientBuilder
        );
    }

    @Bean
    SyntGetConsumer<List<Medlemskapsmelding>> medlConsumer() throws MalformedURLException {
        return new SyntGetConsumer<>(
                applicationManager,
                "synthdata-medl",
                medlUrl,
                true,
                MEDLEMSKAPSMELDING_LIST_TYPE,
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
    SyntGetConsumer<List<String>> navConsumer() throws MalformedURLException {
        return new SyntGetConsumer<>(
                applicationManager,
                "synthdata-nav",
                navEndringsmeldingUrl,
                true,
                STRING_LIST_TYPE,
                webClientBuilder);
    }
    @Bean
    SyntPostConsumer<List<String>, List<Inntektsmelding>> poppConsumer() throws MalformedURLException {
        return new SyntPostConsumer<>(
                applicationManager,
                "synthdata-popp",
                poppUrl,
                true,
                STRING_LIST_TYPE,
                INNTEKTSMELDING_POPP_LIST_TYPE,
                webClientBuilder);
    }

    @Bean
    SyntGetConsumer<List<SamMelding>> samConsumer() throws MalformedURLException {
        return new SyntGetConsumer<>(
                applicationManager,
                "synthdata-sam",
                samUrl,
                true,
                SAM_MELDING_LIST_TYPE,
                webClientBuilder);
    }

    @Bean
    SyntPostConsumer<Map<String, List<no.nav.registre.syntrest.domain.inntekt.Inntektsmelding>>,
                Map<String, List<no.nav.registre.syntrest.domain.inntekt.Inntektsmelding>>> inntektConsumer() throws MalformedURLException {
        return new SyntPostConsumer<>(
                applicationManager,
                "synthdata-inntekt",
                inntektUrl,
                true,
                INNTEKTSMELDING_MAP_TYPE,
                INNTEKTSMELDING_MAP_TYPE,
                webClientBuilder);
    }

    @Bean
    SyntGetConsumer<List<TPmelding>> tpConsumer() throws MalformedURLException {
        return new SyntGetConsumer<>(
                applicationManager,
                "synthdata-tp",
                tpUrl,
                true,
                TP_MELDING_LIST_TYPE,
                webClientBuilder);
    }

    @Bean
    SyntGetConsumer<List<SkdMelding>> tpsConsumer() throws MalformedURLException {
        return new SyntGetConsumer<>(
                applicationManager,
                "synthdata-tps",
                tpsUrl,
                true,
                SKD_MELDING_LIST_TYPE,
                webClientBuilder);
    }

    @Bean
    SyntPostConsumer<Map<String, Integer>, Map<String, List<FrikortKvittering>>> frikortConsumer() throws MalformedURLException {
        return new SyntPostConsumer<>(
                applicationManager,
                "synthdata-frikort",
                frikortUrl,
                true,
                STRING_INTEGER_MAP_TYPE,
                STRING_FRIKORT_KVITTERING_MAP_TYPE,
                webClientBuilder);
    }

    @Bean
    SyntAmeldingConsumer ameldingConsumer() throws MalformedURLException {
        return new SyntAmeldingConsumer(
                applicationManager,
                "synthdata-amelding",
                ameldingUrl,
                false,
                webClientBuilder);
    }
}
