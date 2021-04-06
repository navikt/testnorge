package no.nav.registre.syntrest.config;

import lombok.RequiredArgsConstructor;
import no.nav.registre.syntrest.consumer.SyntAmeldingConsumer;
import no.nav.registre.syntrest.consumer.SyntConsumer;
import no.nav.registre.syntrest.consumer.SyntGetConsumer;
import no.nav.registre.syntrest.consumer.SyntPostConsumer;
import no.nav.registre.syntrest.consumer.SyntPostMapConsumer;
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
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilderFactory;

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


    @Bean UriBuilderFactory uriFactory() {
        return new DefaultUriBuilderFactory();
    }

    @Bean
    @DependsOn({"applicationManager"})
    SyntPostConsumer<Arbeidsforholdsmelding[], String[]> aaregConsumer() {
        return new SyntPostConsumer<Arbeidsforholdsmelding[], String[]>(
                applicationManager,
                "synthdata-aareg",
                aaregUrl,
                true,
                Arbeidsforholdsmelding[].class);
    }

    @Bean
    @DependsOn({"applicationManager"})
    SyntGetConsumer<Barnebidragsmelding[]> bisysConsumer() {
        return new SyntGetConsumer<Barnebidragsmelding[]>(
                applicationManager,
                "synthdata-arena-bisys",
                bisysUrl,
                true,
                Barnebidragsmelding[].class,
                uriFactory());
    }

    @Bean
    @DependsOn({"applicationManager"})
    SyntGetConsumer<Institusjonsmelding[]> instConsumer() {
        return new SyntGetConsumer<Institusjonsmelding[]>(
                applicationManager,
                "synthdata-inst",
                instUrl,
                true,
                Institusjonsmelding[].class,
                uriFactory());
    }

    @Bean
    @DependsOn({"applicationManager"})
    SyntGetConsumer<Medlemskapsmelding[]> medlConsumer() {
        return new SyntGetConsumer<Medlemskapsmelding[]>(
                applicationManager,
                "synthdata-medl",
                medlUrl,
                true,
                Medlemskapsmelding[].class,
                uriFactory());
    }

    @Bean
    @DependsOn({"applicationManager"})
    SyntGetConsumer<String[]> meldekortConsumer() {
        return new SyntGetConsumer<String[]>(
                applicationManager,
                "synthdata-arena-meldekort",
                arenaMeldekortUrl,
                false,
                String[].class,
                uriFactory());
    }

    @Bean
    @DependsOn({"applicationManager"})
    SyntGetConsumer<String[]> navConsumer() {
        return new SyntGetConsumer<String[]>(
                applicationManager,
                "synthdata-nav",
                navEndringsmeldingUrl,
                true,
                String[].class,
                uriFactory());
    }

    @Bean
    @DependsOn({"applicationManager"})
    SyntPostConsumer<Inntektsmelding[], String[]> poppConsumer() {
        return new SyntPostConsumer<Inntektsmelding[], String[]>(
                applicationManager,
                "synthdata-popp",
                poppUrl,
                true,
                Inntektsmelding[].class);
    }

    @Bean
    @DependsOn({"applicationManager"})
    SyntGetConsumer<SamMelding[]> samConsumer() {
        return new SyntGetConsumer<SamMelding[]>(
                applicationManager,
                "synthdata-sam",
                samUrl,
                true,
                SamMelding[].class,
                uriFactory());
    }

    @Bean
    @DependsOn({"applicationManager"})
    SyntPostMapConsumer<Map<String, List<no.nav.registre.syntrest.domain.inntekt.Inntektsmelding>>,
                Map<String, List<no.nav.registre.syntrest.domain.inntekt.Inntektsmelding>>> inntektConsumer() {
        return new SyntPostMapConsumer<Map<String, List<no.nav.registre.syntrest.domain.inntekt.Inntektsmelding>>,
                Map<String, List<no.nav.registre.syntrest.domain.inntekt.Inntektsmelding>>>(
                applicationManager,
                "synthdata-inntekt",
                inntektUrl,
                true);
    }

    @Bean
    @DependsOn({"applicationManager"})
    SyntGetConsumer<TPmelding[]> tpConsumer() {
        return new SyntGetConsumer<TPmelding[]>(
                applicationManager,
                "synthdata-tp",
                tpUrl,
                true,
                TPmelding[].class,
                uriFactory());
    }

    @Bean
    @DependsOn({"applicationManager"})
    SyntGetConsumer<SkdMelding[]> tpsConsumer() {
        return new SyntGetConsumer<SkdMelding[]>(
                applicationManager,
                "synthdata-tps",
                tpsUrl,
                true,
                SkdMelding[].class,
                uriFactory());
    }

    @Bean
    @DependsOn({"applicationManager"})
    SyntPostMapConsumer<Map<String, List<FrikortKvittering>>, Map<String, Integer>> frikortConsumer() {
        return new SyntPostMapConsumer<Map<String, List<FrikortKvittering>>, Map<String, Integer>>(
                applicationManager,
                "synthdata-frikort",
                frikortUrl,
                true);
    }

    @Bean
    @DependsOn({"applicationManager"})
    SyntAmeldingConsumer ameldingConsumer() {
        return new SyntAmeldingConsumer(
                applicationManager,
                "synthdata-amelding",
                ameldingUrl,
                true);
    }

}
